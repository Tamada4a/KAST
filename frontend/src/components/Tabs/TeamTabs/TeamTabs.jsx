import React, { useState } from "react";
import { Routes, Route, NavLink, Navigate, useParams } from 'react-router-dom';
import Description from "./Tabs/Description/Description";
import Achievments from "../Achievments/Achievments";
import Events from "../Events/Events";
import Structure from "./Tabs/Structure/Structure";
import Matches from "../Matches/Matches";
import Editor from "../../Editor/Editor";
import Login from "../../Login/Login"
import { request } from "../../../Utils/MyAxios";
import { showNotification } from "../../../Utils/Utils";
import "./TeamTabs.css";

function TeamTabs(props) {

    const isCapAdmin = props.isCapAdmin;
    const [editorActive, setEditorActive] = useState(false); //состояния модального окна для редактирования описания

    const [valueDecription, setValueDecription] = useState(props.description); //Для селектора команды
    const [settedValueDescription, setSettedValueDescription] = useState(props.description);

    const params = useParams();


    async function handleClick() {
        if (props.description != settedValueDescription) {
            try {
                await request("POST", "/changeDescription", {
                    team: params.id,
                    description: settedValueDescription
                });
                setValueDecription(settedValueDescription);
                showNotification("Описание успешно изменено", "ok");
            } catch (err) {
                showNotification(err.response.data.message, "warn");
            }
        }

        setEditorActive(!editorActive);
    };


    return (
        <div>
            <ul className="teamTab">
                <li className="tab_link" key="TeamTabs Описание">
                    <NavLink to="description" style={({ isActive }) => ({  // если активна, то текст белый
                        color: isActive ? 'var(--text-01)' : 'var(--text-02)'
                    })}>
                        <div className="description_col">
                            Описание
                            {isCapAdmin ? <Editor size="15px" depth={2} onClick={() => { setEditorActive(true); setSettedValueDescription(props.description) }} />
                                : <></>}
                        </div>
                    </NavLink>
                </li>
                <li className="tab_link" key="TeamTabs Матчи">
                    <NavLink to="matches" style={({ isActive }) => ({  // если активна, то текст белый
                        color: isActive ? 'var(--text-01)' : 'var(--text-02)'
                    })}>
                        Матчи
                    </NavLink>
                </li>
                <li className="tab_link" key="TeamTabs Турниры">
                    <NavLink to="events" style={({ isActive }) => ({  // если активна, то текст белый
                        color: isActive ? 'var(--text-01)' : 'var(--text-02)'
                    })}>
                        Турниры
                    </NavLink>
                </li>
                <li className="tab_link" key="TeamTabs Достижения">
                    <NavLink to="achievements" style={({ isActive }) => ({  // если активна, то текст белый
                        color: isActive ? 'var(--text-01)' : 'var(--text-02)'
                    })}>
                        Достижения
                    </NavLink>
                </li>
                <li className="tab_link" key="TeamTabs Состав">
                    <NavLink to="structure" style={({ isActive }) => ({  // если активна, то текст белый
                        color: isActive ? 'var(--text-01)' : 'var(--text-02)'
                    })}>
                        Состав
                    </NavLink>
                </li>
            </ul>
            <Routes>
                <Route index element={<Navigate replace to={`/team/${props.team}/description`} />} />
                <Route path="description" element={<Description desc={valueDecription} />} />
                <Route path="matches" element={<Matches matches_upcoming={props.matches_upcoming} matches_ended={props.matches_ended} type="team" param={props.team} />} />
                <Route path="events" element={<Events ongoing={props.ongoing_events} ended={props.ended_events} type="team" param={props.team} />} />
                <Route path="achievements" element={<Achievments lan={props.lan_events} online={props.online_events} />} />
                <Route path="structure" element={<Structure roster={props.players} ex_players={props.ex_players} />} />
            </Routes>

            {isCapAdmin ?
                <Login active={editorActive} setActive={setEditorActive}>
                    <div className="header_splash_window">
                        <div className="logo_splash_window"></div>
                    </div>
                    <div className="info_text">
                        <p>Укажите информацию о команде</p>
                    </div>
                    <div className="col_center_gap30">
                        <div className="description_field">
                            <textarea type="text" placeholder="Введите описание" style={{ color: "white" }} value={settedValueDescription} onChange={e => setSettedValueDescription(e.target.value)}></textarea>
                        </div>
                        <div className="full_grey_button">
                            <input type="submit" value="Сохранить" onClick={handleClick} />
                        </div>
                    </div>
                </Login>
                :
                <></>
            }
        </div>
    );
}

export default TeamTabs;