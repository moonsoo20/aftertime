package com.aftertime.api.capsule;

import com.aftertime.api.user.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.*;
import java.util.*;

@Service
public class CapsuleService {
    private static final Duration EDIT_GRACE_PERIOD=Duration.ofMinutes(10);
    private final CapsuleRepository repository; private final AppUserRepository users;
    private final Clock clock=Clock.systemUTC();
    public CapsuleService(CapsuleRepository r,AppUserRepository u){repository=r;users=u;}
    private AppUser currentUser(){return users.findByEmailIgnoreCase(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();}
    private Capsule findOwned(UUID id){return repository.findByIdAndOwner(id,currentUser()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"캡슐을 찾을 수 없습니다."));}

    public CapsuleDtos.Summary create(CapsuleDtos.CreateRequest r){Capsule c=repository.save(new Capsule(r.title().trim(),r.recipient().trim(),r.message().trim(),r.unlockAt(),currentUser()));return CapsuleDtos.Summary.from(c,clock.instant());}
    public List<CapsuleDtos.Summary> findAll(){Instant now=clock.instant();return repository.findAllByOwnerOrderByCreatedAtDesc(currentUser()).stream().map(c->CapsuleDtos.Summary.from(c,now)).toList();}
    public CapsuleDtos.ManagementDetail findOne(UUID id){Capsule c=findOwned(id);return CapsuleDtos.ManagementDetail.from(c,clock.instant(),c.getCreatedAt().plus(EDIT_GRACE_PERIOD));}
    public CapsuleDtos.Detail open(UUID id){Capsule c=findOwned(id);if(clock.instant().isBefore(c.getUnlockAt()))throw new ResponseStatusException(HttpStatus.LOCKED,"아직 열 수 없는 캡슐입니다.");return CapsuleDtos.Detail.from(c);}
    public CapsuleDtos.Summary update(UUID id,CapsuleDtos.UpdateRequest r){Capsule c=findOwned(id);Instant now=clock.instant();if(!now.isBefore(c.getCreatedAt().plus(EDIT_GRACE_PERIOD)))throw new ResponseStatusException(HttpStatus.CONFLICT,"수정 가능 시간이 지났습니다.");if(!now.isBefore(c.getUnlockAt()))throw new ResponseStatusException(HttpStatus.CONFLICT,"이미 개봉된 캡슐은 수정할 수 없습니다.");c.update(r.title().trim(),r.recipient().trim(),r.message().trim(),r.unlockAt());return CapsuleDtos.Summary.from(repository.save(c),now);}
    public void delete(UUID id){repository.delete(findOwned(id));}
}
