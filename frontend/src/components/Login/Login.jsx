import React from "react";
import './Login.css'
import './splash_window.css'
import './text_fields.css'
// Компонент модального окна
const Login = ({ active, setActive, children }) => (
    <div className={active ? "authorize active" : "authorize"} onClick={() => setActive(false)} >
        <div className={active ? "splash_window active" : "splash_window"} onClick={e => e.stopPropagation()}>
            {children}
        </div>
    </div>
);

export default Login;
