import {FormEvent, useEffect, useState} from 'react';
import {ArrowRight, CalendarDays, Clock3, LockKeyhole, LogOut, Pencil, Plus, Sparkles, Trash2, X} from 'lucide-react';

type User = { name: string; email: string };
type Capsule = { id: string; title: string; recipient: string; unlockAt: string; createdAt: string; unlocked: boolean };
type Management = Capsule & { message: string | null; editable: boolean; editableUntil: string };
type Opened = { id: string; title: string; recipient: string; message: string; unlockAt: string; createdAt: string };
const API = '/api/capsules';

function remaining(d: string) {
    const m = Math.max(0, new Date(d).getTime() - Date.now());
    return m ? `${Math.floor(m / 86400000)}일 ${Math.floor(m / 3600000) % 24}시간 ${Math.floor(m / 60000) % 60}분` : '지금 열 수 있어요'
}

function localInput(d: string) {
    const x = new Date(d);
    x.setMinutes(x.getMinutes() - x.getTimezoneOffset());
    return x.toISOString().slice(0, 16)
}

export default function App() {
    const [user, setUser] = useState<User | null | undefined>(), [capsules, setCapsules] = useState<Capsule[]>([]), [creating, setCreating] = useState(false), [selected, setSelected] = useState<Management | null>(null), [opened, setOpened] = useState<Opened | null>(null), [, tick] = useState(0);
    const load = () => fetch(API).then(r => r.ok ? r.json() : []).then(setCapsules);
    useEffect(() => {
        fetch('/api/auth/me').then(r => r.ok ? r.json() : null).then(setUser);
        const t = setInterval(() => tick(v => v + 1), 30000);
        return () => clearInterval(t)
    }, []);
    useEffect(() => {
        if (user) load()
    }, [user]);

    async function show(c: Capsule) {
        const r = await fetch(`${API}/${c.id}`);
        if (r.ok) setSelected(await r.json())
    }

    async function open(id: string) {
        const r = await fetch(`${API}/${id}/open`);
        if (r.ok) {
            setOpened(await r.json());
            setSelected(null)
        }
    }

    async function remove(id: string) {
        if (!confirm('이 캡슐을 정말 삭제할까요? 삭제한 캡슐은 복구할 수 없습니다.')) return;
        const r = await fetch(`${API}/${id}`, {method: 'DELETE'});
        if (r.ok) {
            setSelected(null);
            load()
        }
    }

    async function logout() {
        await fetch('/api/auth/logout', {method: 'POST'});
        setUser(null);
        setCapsules([])
    }

    if (user === undefined) return <div className="splash">AFTERTIME</div>;
    if (!user) return <Auth onAuth={setUser}/>;
    return <main>
        <nav><a className="brand" href="#"><span><Sparkles size={17}/></span> AFTERTIME</a>
            <div className="nav-actions"><small>{user.name}님</small>
                <button className="ghost" onClick={() => setCreating(true)}><Plus size={17}/> 새 캡슐</button>
                <button className="icon-button" onClick={logout}><LogOut size={17}/></button>
            </div>
        </nav>
        <section className="hero">
            <div className="eyebrow"><span/> A LETTER ACROSS TIME</div>
            <h1>오늘의 마음을<br/><em>미래로 보내세요.</em></h1><p>소중한 순간과 아직 하지 못한 말을 담아두세요.<br/>시간이 흐른 뒤, 가장 완벽한 순간에 도착합니다.</p>
            <button className="primary" onClick={() => setCreating(true)}>타임캡슐 만들기 <ArrowRight size={18}/></button>
            <div className="orbit">
                <div className="ring ring-a"/>
                <div className="ring ring-b"/>
                <div className="orb"><LockKeyhole size={30}/><small>SEALED</small></div>
            </div>
        </section>
        <section className="library">
            <div className="section-title">
                <div><span>YOUR CAPSULES</span><h2>시간 속에 보관된 이야기</h2></div>
                <p>{capsules.length}개의 캡슐</p></div>
            <div className="grid">{!capsules.length &&
                <button className="empty" onClick={() => setCreating(true)}><Plus/><strong>첫 번째 캡슐 만들기</strong><span>미래의 나에게 편지를 남겨보세요</span>
                </button>}{capsules.map((c, i) => {
                const ready = new Date(c.unlockAt).getTime() <= Date.now();
                return <article className={`card tone-${i % 3} ${ready ? 'ready' : ''}`} key={c.id}
                                onClick={() => show(c)}>
                    <div className="card-top"><span>{ready ? 'READY TO OPEN' : 'SEALED'}</span>{ready ? <Sparkles/> :
                        <LockKeyhole/>}</div>
                    <h3>{c.title}</h3><p>To. {c.recipient}</p>
                    <div className="timer"><Clock3/>
                        <div><small>{ready ? '도착했습니다' : '개봉까지'}</small><strong>{remaining(c.unlockAt)}</strong></div>
                    </div>
                    <footer>상세 보기 <ArrowRight/></footer>
                </article>
            })}</div>
        </section>
        <footer className="page-footer">Made for moments worth remembering · AFTERTIME</footer>
        {creating && <CapsuleForm close={() => setCreating(false)} saved={() => {
            setCreating(false);
            load()
        }}/>}{selected && <DetailModal capsule={selected} close={() => setSelected(null)} open={() => open(selected.id)}
                                       remove={() => remove(selected.id)} saved={() => {
        setSelected(null);
        load()
    }}/>}{opened && <div className="modal-backdrop">
        <div className="letter">
            <button className="close" onClick={() => setOpened(null)}><X/></button>
            <span>DELIVERED FROM THE PAST</span><h2>{opened.title}</h2><p className="to">To. {opened.recipient}</p>
            <div className="message">{opened.message}</div>
            <small>봉인한 날 · {new Date(opened.createdAt).toLocaleDateString('ko-KR')}</small></div>
    </div>}</main>
}

function DetailModal({capsule, close, open, remove, saved}: {
    capsule: Management;
    close: () => void;
    open: () => void;
    remove: () => void;
    saved: () => void
}) {
    const [editing, setEditing] = useState(false);
    if (editing && capsule.editable) return <CapsuleForm capsule={capsule} close={() => setEditing(false)}
                                                         saved={saved}/>;
    return <div className="modal-backdrop">
        <section className="detail-modal">
            <button className="close" onClick={close}><X/></button>
            <span className="detail-label">{capsule.unlocked ? 'READY TO OPEN' : 'SEALED CAPSULE'}</span>
            <h2>{capsule.title}</h2><p className="to">To. {capsule.recipient}</p>
            <div className="detail-status">
                <div>
                    <CalendarDays/><small>개봉일</small><strong>{new Date(capsule.unlockAt).toLocaleString('ko-KR')}</strong>
                </div>
                <div><Clock3/><small>남은 시간</small><strong>{remaining(capsule.unlockAt)}</strong></div>
            </div>
            {capsule.editable ? <p className="edit-notice">생성 후 10분 동안 수정할 수
                    있어요. {new Date(capsule.editableUntil).toLocaleTimeString('ko-KR')}까지</p> :
                <p className="sealed-notice"><LockKeyhole/> 캡슐이 완전히 봉인되어 내용은 개봉일까지 숨겨집니다.</p>}
            <div className="detail-actions">{capsule.editable &&
                <button className="secondary" onClick={() => setEditing(true)}><Pencil/> 수정</button>}
                <button className="danger" onClick={remove}><Trash2/> 삭제</button>
                {capsule.unlocked &&
                    <button className="primary open-button" onClick={open}>편지 열기 <ArrowRight/></button>}</div>
        </section>
    </div>
}

function CapsuleForm({close, saved, capsule}: { close: () => void; saved: () => void; capsule?: Management }) {
    const d = new Date(Date.now() + 86400000);
    d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
    const [error, setError] = useState('');

    async function submit(e: FormEvent<HTMLFormElement>) {
        e.preventDefault();
        const f = new FormData(e.currentTarget), r = await fetch(capsule ? `${API}/${capsule.id}` : API, {
            method: capsule ? 'PUT' : 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                title: f.get('title'),
                recipient: f.get('recipient'),
                message: f.get('message'),
                unlockAt: new Date(String(f.get('unlockAt'))).toISOString()
            })
        });
        if (r.ok) saved(); else setError(r.status === 409 ? '수정 가능 시간이 지났습니다.' : '저장하지 못했습니다. 입력값을 확인해주세요.')
    }

    return <div className="modal-backdrop">
        <form className="create" onSubmit={submit}>
            <button type="button" className="close" onClick={close}><X/></button>
            <span>{capsule ? 'EDIT TIME CAPSULE' : 'NEW TIME CAPSULE'}</span><h2>{capsule ? '봉인 전 이야기를 수정하세요.' : <>미래로
            보낼 이야기를<br/>담아주세요.</>}</h2><label>캡슐 이름<input name="title" required maxLength={80}
                                                          defaultValue={capsule?.title}/></label><label>받는 사람<input
            name="recipient" required maxLength={80} defaultValue={capsule?.recipient}/></label><label>편지<textarea
            name="message" required maxLength={4000} defaultValue={capsule?.message ?? ''}/></label><label>개봉 날짜<input
            name="unlockAt" type="datetime-local" required min={d.toISOString().slice(0, 16)}
            defaultValue={capsule ? localInput(capsule.unlockAt) : d.toISOString().slice(0, 16)}/></label>{error &&
            <p className="form-error">{error}</p>}
            <button className="primary submit">{capsule ? '수정 내용 저장' : '캡슐 봉인하기'} <LockKeyhole size={18}/></button>
        </form>
    </div>
}

function Auth({onAuth}: { onAuth: (u: User) => void }) {
    const [signup, setSignup] = useState(false), [error, setError] = useState('');

    async function submit(e: FormEvent<HTMLFormElement>) {
        e.preventDefault();
        const d = new FormData(e.currentTarget), r = await fetch(`/api/auth/${signup ? 'signup' : 'login'}`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({name: d.get('name'), email: d.get('email'), password: d.get('password')})
        });
        if (r.ok) onAuth(await r.json()); else setError(r.status === 409 ? '이미 가입된 이메일입니다.' : '이메일 또는 비밀번호를 확인해주세요.')
    }

    return <main className="auth-page">
        <form className="auth-card" onSubmit={submit}><a className="brand"><span><Sparkles size={17}/></span> AFTERTIME</a>
            <h1>{signup ? '시간 여행을 시작하세요.' : '다시 만나 반가워요.'}</h1><p>당신의 타임캡슐이 기다리고 있어요.</p>{signup &&
                <label>이름<input name="name" required/></label>}<label>이메일<input name="email" type="email"
                                                                                required/></label><label>비밀번호<input
                name="password" type="password" required minLength={8}/></label>{error &&
                <p className="auth-error">{error}</p>}
            <button className="primary auth-submit">{signup ? '회원가입' : '로그인'} <ArrowRight size={18}/></button>
            <button type="button" className="auth-switch" onClick={() => {
                setSignup(!signup);
                setError('')
            }}>{signup ? '이미 계정이 있나요? 로그인' : '처음이신가요? 회원가입'}</button>
        </form>
    </main>
}
