import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import Editor from "../../../components/Editor/Editor";
import SetMatchInfo from "../../../components/MatchHelper/SetMatchInfo";
import Login from "../../../components/Login/Login";
import PlayerLogoWrapper from "../../../components/PlayerWrapper/PlayerLogoWrapper";
import { fillSpaces, toggleOffSelectors, showNotification } from "../../../Utils/Utils";
import { request } from "../../../Utils/MyAxios";
import "./MatchHeader.css"
import Timer from "../../../components/Timer/Timer";

function MatchHeader(props) {

    const [teamsActive, setTeamsActive] = useState(null); // Состояния команд - выбрана ли команда(чтоб блокировать ее) в селекторе
    const [matchEditorActive, setMatchEditorActive] = useState(false); //состояния модального окна для редактирования текущих матчей
    const [deleteMatchActive, setDeleteMatchActive] = useState(false);

    const [valueDate, setValueDate] = useState(null); // Это для даты выбранного матча
    const [dateSelected, setDateSelected] = useState(null); // здесь хранится выбраная дата
    const [dateSelectorActive, setDateSelectorActive] = useState(false); // открыт/закрыт календарь

    const [valueTime, setValueTime] = useState(null); // Это для времени выбранного матча
    const [timeSelected, setTimeSelected] = useState(null); // здесь хранится выбраное время
    const [timeSelectorActive, setTimeSelectorActive] = useState(false); // открыт/закрыт выбор времени

    const [eventSelected, setEventSelected] = useState(null); // здесь хранится выбраный турнир
    const [eventSelectorActive, setEventSelectorActive] = useState(false); // открыт/закрыт выбор турнира

    const [selectedLeftTeam, setSelectedLeftTeam] = useState(null); // левая команда
    const [selectedRightTeam, setSelectedRightTeam] = useState(null); // правая команда    

    const [leftTeamSelectorActive, setLeftTeamSelectorActive] = useState(false); // открыт/закрыт выбор левой команды
    const [rightTeamSelectorActive, setRightTeamSelectorActive] = useState(false); // открыт/закрыт выбор правой команды

    const navigate = useNavigate();


    useEffect(() => {
        let temp = Array(props.teams.length).fill(false);
        props.teams.map((team, index) => {
            if ((team.name === props.match.firstTeam.name) || (team.name === props.match.secondTeam.name)) {
                temp[index] = true;
            }
        })
        setTeamsActive(temp);


        setValueDate(getMatchDate());
        setDateSelected(getMatchDate());

        setValueTime(getMatchTime());
        setTimeSelected(getMatchTime());

        setEventSelected(props.match.event);

        setSelectedLeftTeam(props.match.firstTeam.name);

        setSelectedRightTeam(props.match.secondTeam.name);
    }, []);


    function monthWord(monthNum) {
        switch (monthNum) {
            case 0: return "января";
            case 1: return "февраля";
            case 2: return "марта";
            case 3: return "апреля";
            case 4: return "мая";
            case 5: return "июня";
            case 6: return "июля";
            case 7: return "августа";
            case 8: return "сентября";
            case 9: return "октября";
            case 10: return "ноября";
            case 11: return "декабря";
        }

    }


    function getMatchDate() {
        const date = props.match.matchDate;

        let day = date.getDate();
        day = day < 10 ? (`0${day}`) : day;

        let month = date.getMonth() + 1;
        month = month < 10 ? (`0${month}`) : month;

        return `${day}.${month}.${date.getFullYear()}`;
    }


    function getMatchTime() {
        const date = props.match.matchDate;

        let hours = date.getHours();
        hours = hours < 10 ? (`0${hours}`) : hours;

        let minutes = date.getMinutes();
        minutes = minutes < 10 ? (`0${minutes}`) : minutes;

        return `${hours}:${minutes}`;
    }


    function toggleOff(idxArr) {
        let toggleDict = {
            0: setDateSelectorActive,
            1: setTimeSelectorActive,
            2: setLeftTeamSelectorActive,
            3: setRightTeamSelectorActive,
            4: setEventSelectorActive
        }

        toggleOffSelectors(toggleDict, idxArr);
    }


    function toggleDate() {
        setDateSelectorActive(!dateSelectorActive);

        toggleOff([1, 2, 3, 4]);
    };


    function toggleTime() {
        setTimeSelectorActive(!timeSelectorActive);

        toggleOff([0, 2, 3, 4]);
    }


    function toggleLeftTeam() {
        setLeftTeamSelectorActive(!leftTeamSelectorActive);

        toggleOff([0, 1, 3, 4]);
    }


    function toggleRightTeam() {
        setRightTeamSelectorActive(!rightTeamSelectorActive);

        toggleOff([0, 1, 2, 4]);
    }


    function indexOf(value) {
        for (let i = 0; i < props.teams.length; ++i) {
            if (value === props.teams[i].name) {
                return i;
            }
        }
    }


    function setTeam(id, value) { // с её помощью делаем нужную команду заблокированной 
        let temp = [...teamsActive];
        let val = indexOf(value);

        temp[val] = !temp[val];
        temp[id] = !temp[id];
        setTeamsActive(temp);
    }


    async function onMatchDelete() {
        try {
            await request("POST", `deleteMatch/${props.match.event}/${props.matchId}`, {});
            navigate("/");
        } catch (err) {
            showNotification(err.response.data.message, "warn");
        }
    }


    function onMatchEdit() {
        setDateSelectorActive(false);
        setDateSelected(valueDate);

        setTimeSelectorActive(false);
        setTimeSelected(valueTime);

        setEventSelected(props.match.event);
        setEventSelectorActive(false);

        setLeftTeamSelectorActive(false);
        setSelectedLeftTeam(props.match.firstTeam.name);

        setRightTeamSelectorActive(false);
        setSelectedRightTeam(props.match.secondTeam.name);

        setMatchEditorActive(true);
    }


    return (
        <div>
            <div className="header_match">
                <div className="container_time_match">
                    <div className="container_time_match_time">
                        <a>
                            {getMatchTime()}
                        </a>
                    </div>
                    <div className="container_time_match_date">
                        <a>{props.match.matchDate.getDate()} {monthWord(props.match.matchDate.getMonth())} {props.match.matchDate.getFullYear()}</a>
                    </div>
                    <div className="container_time_match_cup">
                        <Link to={`/event/${fillSpaces(props.match.event)}`}>{props.match.event}</Link>
                    </div>
                    <div className="container_time_match_live">
                        <div className="row_center_5px">
                            {props.match.matchStatus === 0 ? <Timer date={props.match.matchDate} style={{ fontSize: "20px" }} /> : null}
                            {props.match.matchStatus === 1 ? <a>LIVE</a> : null}
                            {props.match.matchStatus === 2 ? <a>Матч окончен</a> : null}
                            {props.isAdmin && props.match.matchStatus !== 2 ? <Editor size="15px" depth={2} onClick={() => onMatchEdit()} /> : <></>}
                        </div>
                    </div>
                </div>
                <div className="flag_team" style={{ background: `linear-gradient(to right, rgba(0, 0, 0, 0.4), rgba(25, 25, 25, 1)), url("${props.match.firstTeam.flagPath}")`, left: "0" }}>
                    {props.match.partType === "team" ?
                        <div className="match_header_team">
                            <Link to={`/team/${props.match.firstTeam.name}`} style={{ textDecoration: "none" }} target="_blank" rel="noopener noreferrer" className="match_header_team">
                                <img src={props.match.firstTeam.logo} alt={props.match.firstTeam.name} />
                                <p>{props.match.firstTeam.name}</p>
                            </Link>
                            {(props.match.matchStatus === 2) ? <a style={{ color: props.match.secondTeam.score < props.match.firstTeam.score ? "green" : "red" }}>{props.match.firstTeam.score}</a> : null}
                        </div>
                        :
                        <div className="match_header_team">
                            <Link to={`/player/${props.match.firstTeam.name}`} style={{ textDecoration: "none" }} target="_blank" rel="noopener noreferrer" className="match_header_team">
                                <PlayerLogoWrapper issuer={"matchHeader"} teamSrc={props.match.firstTeam.teamSrc} team={props.match.firstTeam.team} playerSrc={props.match.firstTeam.logo} player={props.match.firstTeam.name} />
                            </Link>
                            {(props.match.matchStatus === 2) ? <a style={{ color: props.match.secondTeam.score < props.match.firstTeam.score ? "green" : "red" }}>{props.match.firstTeam.score}</a> : null}
                        </div>
                    }
                </div>
                <div className="flag_team" style={{ background: `linear-gradient(to left, rgba(0, 0, 0, 0.4), rgba(25, 25, 25, 1)), url("${props.match.secondTeam.flagPath}")`, right: "0" }}>
                    {props.match.partType === "team" ?
                        <div className="match_header_team">
                            <Link to={`/team/${props.match.secondTeam.name}`} style={{ textDecoration: "none" }} target="_blank" rel="noopener noreferrer" className="match_header_team">
                                <img src={props.match.secondTeam.logo} alt={props.match.secondTeam.name} />
                                <p>{props.match.secondTeam.name}</p>
                            </Link>
                            {(props.match.matchStatus === 2) ? <a style={{ color: props.match.secondTeam.score < props.match.firstTeam.score ? "red" : "green" }}>{props.match.secondTeam.score}</a> : null}
                        </div>
                        :
                        <div className="match_header_team">
                            <Link to={`/player/${props.match.secondTeam.name}`} style={{ textDecoration: "none" }} target="_blank" rel="noopener noreferrer" className="match_header_team">
                                <PlayerLogoWrapper issuer={"matchHeader"} teamSrc={props.match.secondTeam.teamSrc} team={props.match.secondTeam.team} playerSrc={props.match.secondTeam.logo} player={props.match.secondTeam.name} />
                            </Link>
                            {(props.match.matchStatus === 2) ? <a style={{ color: props.match.secondTeam.score < props.match.firstTeam.score ? "red" : "green" }}>{props.match.secondTeam.score}</a> : null}
                        </div>
                    }
                </div>
            </div>

            {
                props.isAdmin && teamsActive !== null && valueDate !== null && dateSelected !== null &&
                    valueTime !== null && timeSelected !== null && eventSelected !== null &&
                    selectedLeftTeam !== null && selectedRightTeam !== null ?
                    <Login active={matchEditorActive} setActive={setMatchEditorActive}>
                        <SetMatchInfo
                            matchId={props.matchId}
                            toggleDate={toggleDate}
                            dateSelected={dateSelected}
                            valueDate={valueDate}
                            dateSelectorActive={dateSelectorActive}
                            setDateSelected={setDateSelected}
                            timeSelected={timeSelected}
                            valueTime={valueTime}
                            timeSelectorActive={timeSelectorActive}
                            setTimeSelected={setTimeSelected}
                            toggleTime={toggleTime}
                            eventSelected={eventSelected}
                            valueEvent={props.match.event}
                            eventSelectorActive={eventSelectorActive}
                            setEventSelectorActive={setEventSelectorActive}
                            setEventSelected={setEventSelected}
                            selectedLeftTeam={selectedLeftTeam}
                            valueLeftTeam={props.match.firstTeam.name}
                            leftTeamSelectorActive={leftTeamSelectorActive}
                            toggleLeftTeam={toggleLeftTeam}
                            setSelectedLeftTeam={setSelectedLeftTeam}
                            setTeam={setTeam}
                            selectedRightTeam={selectedRightTeam}
                            valueRightTeam={props.match.secondTeam.name}
                            rightTeamSelectorActive={rightTeamSelectorActive}
                            toggleRightTeam={toggleRightTeam}
                            setSelectedRightTeam={setSelectedRightTeam}
                            setMatchEditorActive={setMatchEditorActive}
                            setDeleteMatchActive={setDeleteMatchActive}
                            teamsActive={teamsActive}
                            teams={props.teams}
                            allEvents={props.allEvents}
                            setMatch={props.setMatch}
                            match={props.match}
                        />
                    </Login>
                    :
                    <></>
            }

            {
                props.isAdmin ?
                    <Login active={deleteMatchActive} setActive={setDeleteMatchActive}>
                        <div className="header_splash_window">
                            <div className="logo_splash_window"></div>
                        </div>
                        <div className="info_text">
                            <p>{`Вы уверены, что хотите удалить матч ${selectedLeftTeam} vs. ${selectedRightTeam}?`}</p>
                        </div>
                        <div className="small_buttons_wrapper">
                            <div className="small_dark_button">
                                <input type="submit" value="Нет" onClick={() => { setDeleteMatchActive(false) }} />
                            </div>
                            <div className="small_grey_button">
                                <input type="submit" value="Да" onClick={() => { setDeleteMatchActive(false); onMatchDelete() }} />
                            </div>
                        </div>
                    </Login>
                    :
                    <></>
            }
        </div >
    )
}

export default MatchHeader;