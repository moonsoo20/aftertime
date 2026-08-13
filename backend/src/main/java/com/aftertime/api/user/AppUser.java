package com.aftertime.api.user;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="users", uniqueConstraints=@UniqueConstraint(columnNames="email"))
public class AppUser {
 @Id private UUID id; @Column(nullable=false,length=100) private String name;
 @Column(nullable=false,length=200) private String email; @Column(nullable=false) private String passwordHash;
 @Column(nullable=false,updatable=false) private Instant createdAt;
 protected AppUser() {} public AppUser(String n,String e,String p){id=UUID.randomUUID();name=n;email=e;passwordHash=p;createdAt=Instant.now();}
 public UUID getId(){return id;} public String getName(){return name;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;}
}
