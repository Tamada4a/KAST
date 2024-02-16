import React, { useState } from "react";
import { Link } from "react-router-dom";
import { fillSpaces, showNotification } from "../../Utils/Utils";
import Login from "../Login/Login";
import { request } from "../../Utils/MyAxios";
import "../../pages/Team/Player/Player.css"
import "./Participant.css"
import PlayerLogoWrapper from "../PlayerWrapper/PlayerLogoWrapper";

function Participant(props) {
    const [mouseOutCard, setMouseOutCard] = useState(true); //Для ховера игрока
    const [mouseOnCard, setMouseOnCard] = useState(false); //Для ховера игрока

    const [deleteActive, setDeleteActive] = useState(false);
    const [acceptActive, setAcceptActive] = useState(false);
    const [kickActive, setKickActive] = useState(false);

    function getClassName(status) {
        switch (status) {
            case "accepted":
                return "accepted_participant";
            case "await":
                return "await_participant";
            case "kicked":
                return "kicked_participant";
            case "":
                return "tba_participant";
        }
    }


    function checkStatus(status) {
        if (props.status === "upcoming") {
            return status === "await" || status === "accepted";
        }
        return props.status === "ongoing" && status !== "kicked";
    }


    function getOnClick(status) {
        if (props.status === "upcoming") {
            if (status === "await") {
                setAcceptActive(true);
            } else {
                setDeleteActive(true);
            }
        } else if (status !== "kicked") {
            setKickActive(true);
        }
    }


    // тут мы отрисовываем в конец пустые места
    function tba_participant() {
        let content = [];
        let size = props.total - props.partLength;
        for (let i = 0; i < size; ++i) {
            content.push(
                <div className="tba_participant" key={`TBA_${i}`}>
                    <div className="tba_wrapper">
                        <div className="participant_team">
                            <img src="../../img/teams_logo/TBAParticipant.svg" alt="Неизвестно" />
                        </div>
                        <p>Неизвестно</p>
                    </div>
                </div>
            );
        }
        return content;
    }


    function getTeamParticipant(src, name) {
        return (
            <div className="participant_wrapper">
                <div className="participant_team">
                    <img src={src} alt={name} />
                </div>
                <p>{name}</p>
            </div>
        );
    }


    function getPlayerParticipant(team, teamsrc, src, name) {
        return (
            <div className="participant_wrapper">
                {team === "" ? <></> :
                    <div className="participant_player_logo"><img src={teamsrc} alt={team} /></div>
                }
                <div className="participant_player">
                    <div className="crop_participant_player"><img src={src} alt={name} /></div>
                </div>
                <p>{name}</p>
            </div>
        );
    }


    // определяем тип участника (команда или игрок)
    function participant_type(participant) {
        let src = participant.logo;
        let name = participant.teamName;
        if (participant.type === "team") {
            if (props.isAdmin && checkStatus(participant.status)) {
                return getTeamParticipant(src, name);
            } else {
                return (
                    <Link to={`/team/${fillSpaces(name)}`} style={{ textDecoration: "none" }}>
                        {getTeamParticipant(src, name)}
                    </Link>
                );
            }
        }
        else {
            if (props.isAdmin && checkStatus(participant.status)) {
                // return getPlayerParticipant(participant.team, participant.teamSrc, src, name);
                return <PlayerLogoWrapper issuer={"participant"} teamSrc={participant.teamSrc} team={participant.team} playerSrc={src} player={name} />;
            } else {
                return (
                    <Link to={`/player/${name}`} style={{ textDecoration: "none" }}>
                        {/* {getPlayerParticipant(participant.team, participant.teamSrc, src, name)} */}
                        <PlayerLogoWrapper issuer={"participant"} teamSrc={participant.teamSrc} team={participant.team} playerSrc={src} player={name} />
                    </Link>
                );
            }
        }
    }


    function getHoverImage(status) {
        if (props.status === "upcoming") {
            if (status === "accepted") {
                return (<img className="kick_hover" style={{ height: "120px", width: "120px" }} src="../../img/KickHovered.svg" alt="Удаление команды" />);
            }
            return (
                <img className="await_hover" src="../../img/Editor.svg" alt="Рассмотренеи заявки" />
            );
        }

        if (status !== "kicked") {
            return (<img className="kick_hover" style={{ height: "120px", width: "120px" }} src="../../img/KickHovered.svg" alt="Удаление команды" />);
        }
    }


    function toggleOnMouseOver(participant) {
        return (
            mouseOutCard ?
                participant_type(participant)
                :
                <div className="img_hover_wrapper_participant" onMouseOut={() => { setMouseOutCard(true); setMouseOnCard(false) }} onClick={() => getOnClick(participant.status)}>
                    <div style={{ opacity: "0.4" }}>
                        {participant_type(participant)}
                    </div>
                    {getHoverImage(participant.status)}
                </div>
        );
    }


    async function deleteParticipant(participantName) {
        try {
            await request("POST", `deleteRequest/${props.tournament.event}/${participantName}`);

            const filteredParticipants = props.allParts.filter(player => !(player.teamName.includes(participantName)));

            props.setTournament({
                ...props.tournament,
                participants: filteredParticipants,
                registred: props.tournament.registred - 1
            });

           showNotification(`${participantName} удалён с турнира`, "ok");
        } catch (err) {
           showNotification(err.response.data.message, "warn");
        }
    }


    async function acceptParticipant(participantName) {
        try {
            await request("POST", `editRequestStatus/${props.tournament.event}/${participantName}/accepted`);
            const filteredParticipants = props.allParts.map((part) => (
                part.teamName === participantName
                    ? { ...part, status: "accepted" }
                    : part
            ));

            props.setTournament({
                ...props.tournament,
                participants: filteredParticipants
            });

           showNotification(`${participantName} принят на турнир`, "ok");
        } catch (err) {
           showNotification(err.response.data.message, "warn");
        }
    }


    async function denyParticipant(participantName) {
        try {
            await request("POST", `deleteRequest/${props.tournament.event}/${participantName}`);
            props.setTournament({
                ...props.tournament,
                participants: props.tournament.participants.filter(part => !(part.teamName.includes(participantName))),
                registred: props.tournament.registred - 1
            });
           showNotification(`${participantName} удалён с турнира`, "ok");
        } catch (err) {
           showNotification(err.response.data.message, "warn");
        }
    }


    async function kickParticipant(participantName) {
        try {
            await request("POST", `editRequestStatus/${props.tournament.event}/${participantName}/kicked`);
            const filteredParticipants = props.allParts.map((part) => (
                part.teamName === participantName
                    ? { ...part, status: "kicked" }
                    : part
            ));

            props.setTournament({
                ...props.tournament,
                participants: filteredParticipants
            });

           showNotification(`${participantName} исключён с турнира`, "ok");
        } catch (err) {
           showNotification(err.response.data.message, "warn");
        }
    }


    function drawParticipants() {
        if (props.total) {
            return tba_participant()
        }

        return (
            <div>
                <div className={getClassName(props.part.status)} onMouseOut={() => { setMouseOutCard(true); setMouseOnCard(false) }} onMouseOver={() => { setMouseOutCard(false); setMouseOnCard(true) }}>
                    {props.isAdmin && checkStatus(props.part.status) ? toggleOnMouseOver(props.part) : participant_type(props.part)}
                </div>

                {props.isAdmin ?
                    <Login active={deleteActive} setActive={setDeleteActive}>
                        <div className="header_splash_window">
                            <div className="logo_splash_window"></div>
                        </div>
                        <div className="info_text">
                            <p>Вы уверены, что хотите отказать {props.part.type === "team" ? "команде" : "игроку"} {props.part.teamName} в участии?</p>
                        </div>
                        <div className="small_buttons_wrapper">
                            <div className="small_dark_button">
                                <input type="submit" value="Нет" onClick={() => deleteActive ? setDeleteActive(!deleteActive) : null} />
                            </div>
                            <div className="small_grey_button">
                                <input type="submit" value="Да" onClick={() => { deleteParticipant(props.part.teamName); setDeleteActive(false) }} />
                            </div>
                        </div>
                    </Login>
                    :
                    <></>
                }

                {props.isAdmin ?
                    <Login active={acceptActive} setActive={setAcceptActive}>
                        <div className="header_splash_window">
                            <div className="logo_splash_window"></div>
                        </div>
                        <div className="info_text">
                            <p>Допустить {props.part.type === "team" ? "команду" : "игрока"} {props.part.teamName} к участию в турнире?</p>
                        </div>
                        <div className="small_buttons_wrapper">
                            <div className="small_dark_button">
                                <input type="submit" value="Нет" onClick={() => { denyParticipant(props.part.teamName); setAcceptActive(false); }} />
                            </div>
                            <div className="small_grey_button">
                                <input type="submit" value="Да" onClick={() => { acceptParticipant(props.part.teamName); setAcceptActive(false) }} />
                            </div>
                        </div>
                    </Login>
                    :
                    <></>
                }

                {props.isAdmin ?
                    <Login active={kickActive} setActive={setKickActive}>
                        <div className="header_splash_window">
                            <div className="logo_splash_window"></div>
                        </div>
                        <div className="info_text">
                            <p>Вы уверены, что хотите исключить {props.part.type === "team" ? "команду" : "игрока"} {props.part.teamName}?</p>
                        </div>
                        <div className="small_buttons_wrapper">
                            <div className="small_dark_button">
                                <input type="submit" value="Нет" onClick={() => kickActive ? setKickActive(!kickActive) : null} />
                            </div>
                            <div className="small_grey_button">
                                <input type="submit" value="Да" onClick={() => { kickParticipant(props.part.teamName); setKickActive(false) }} />
                            </div>
                        </div>
                    </Login>
                    :
                    <></>
                }
            </div>
        );
    }


    return drawParticipants();
}

export default Participant;