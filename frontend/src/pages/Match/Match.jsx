import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import "./Match.css"
import "./Statistic/TeamBlock/TeamRow/TeamRow.css";
import MatchHeader from "./MatchHeader/MatchHeader";
import MatchMap from "./MatchMap/MatchMap"
import Streams from "./Streams/Streams"
import Description from "./Description/Description";
import Scoreboard from "./Scoreboard/Scoreboard"
import ScrollLog from "./ScrollLog/ScrollLog"
import Statistic from "./Statistic/Statistic"
import Editor from "../../components/Editor/Editor";
import Login from "../../components/Login/Login";
import SearchableArraySelector from "../../components/Selector/SearchableArraySelector";
import NonSelectableSelector from "../../components/Selector/NonSelectableSelector";
import ReactTwitchEmbedVideo from "react-twitch-embed-video";
import ReactPlayer from 'react-player';
import { isNotInList, showNotification, isEmpty, getItemFromDictByValue, getImage, getTeamSrc, getStoredPlayerNick } from "../../Utils/Utils";
import { applHeaders, request } from "../../Utils/MyAxios";
import TeamRosters from "./TeamRosters/TeamRosters";
import Preloader from "../../components/Preloader/Preloader";

function Match() {

    const params = useParams();

    const [match, setMatch] = useState(null);

    const [isAdmin, setIsAdmin] = useState(false);
    const [isParticipant, setIsParticipant] = useState(false);

    const [countries, setCountries] = useState(null);

    const [valueStreams, setValueStreams] = useState([]);
    const [selectorCountryActive, setSelectorCountryActive] = useState([]); // Состояния селектора стран

    const [ipValue, setIpValue] = useState("");
    const [ipMatch, setActiveIpMatch] = useState(false);

    const [activeLink, setActiveLink] = useState(null);

    const [streams, setStreams] = useState(null); // стримы
    const [matchStreams, setMatchStreams] = useState(null);
    const [streamsEditorActive, setStreamsEditorActive] = useState(false); //Состояния модального окна для редактирования, добавления стримов

    const [teams, setTeams] = useState(null);

    const [allEvents, setAllEvents] = useState(null);


    async function getStreams() {
        let streams = await request("GET", `getMatchStreams/${params.id}`, {}, applHeaders);
        setMatchStreams(streams.data === "" ? null : streams.data);
        setStreams(streams.data === "" ? null : streams.data);
    }


    async function getEvents(allEvents) {
        setAllEvents(await Promise.all(allEvents.map(async event => ({
            ...event,
            eventSrc: await getImage(event.name)
        }))))
    }


    async function getTeams(teams, partType) {
        setTeams(await Promise.all(teams.map(async team => (await getTeam(team, partType)))))
    }


    async function getFullMatch() {
        let match = await request("GET", `getFullMatch/${params.id}/${getStoredPlayerNick()}`, {}, applHeaders);

        let partType = match.data.match.partType;

        getEvents(match.data.allEvents);

        getTeams(match.data.teams, partType);

        let firstTeam = await getMatchTeam(match.data.match.firstTeam, partType);
        let secondTeam = await getMatchTeam(match.data.match.secondTeam, partType);

        setMatch({
            ...match.data.match,
            firstTeam: firstTeam,
            secondTeam: secondTeam,
            matchDate: await getMatchDate(match.data.match.matchDate)
        });

        setCountries(match.data.countries);

        setIpValue(match.data.match.ip);

        setIsAdmin(match.data.isAdmin);

        setIsParticipant(match.data.isParticipant);
    }


    async function getMatchDate(date) {
        return new Date(date[0], date[1] - 1, date[2], date[3], date[4], 0);
    }


    async function getMatchTeam(team, partType) {
        if (partType === "team") {
            let teamWithImg = await getTeam(team, partType);
            return {
                ...teamWithImg,
                players: [...teamWithImg.players, ...Array(5 - teamWithImg.players.length).fill({ name: "TBA" })]
            }
        }

        return await getTeam(team, partType);
    }


    async function getTeam(team, partType) {
        return {
            ...team,
            logo: await getImage(team.name),
            teamSrc: await getTeamSrc(team.team, partType),
            players: await Promise.all(team.players.map(async player => ({
                ...player,
                src: await getImage(player.name)
            })))
        }
    }

    useEffect(() => {
        getFullMatch();
        getStreams();
    }, []);


    function isLastFinished(idx) {
        let elem = valueStreams[idx];

        return !isEmpty(elem.country, "str") && !isEmpty(elem.name, "str") && !isEmpty(elem.link, "str") && elem.link.includes("https://www.") && (elem.link.includes("youtube.com/watch?v=") || elem.link.includes("twitch.tv/")) && !isNotInList(countries, "countryRU", elem.country);
    }


    function addStream() {
        if (valueStreams.length > 0 && !isLastFinished(valueStreams.length - 1)) {
            showNotification("Вы не заполнили предыдущий стрим", "warn");
        } else {
            let tempStreams = [...valueStreams];
            tempStreams.push({ country: "Выберите страну", name: "", link: "", flagPath: "" });
            setValueStreams(tempStreams);

            let tempCountryActive = [...selectorCountryActive]
            tempCountryActive.push(false);
            setSelectorCountryActive(tempCountryActive);
        }
    }


    function removeStream(streamIndex, type) {
        if (type === "valueStreams") {
            setValueStreams(valueStreams.filter((stream, index) => index !== streamIndex));
            setSelectorCountryActive(selectorCountryActive.filter((active, index) => index !== streamIndex));
        } else {
            setStreams(streams.filter((stream, index) => index !== streamIndex))
        }
    };


    function setChangedTextInput(id, value, key) {
        let tempStreams = [...valueStreams];
        tempStreams[id][key] = value;
        setValueStreams(tempStreams);
    }


    function getByWhoPicked(map) {
        if (map === "TBA" || match.picks === null) {
            return null;
        }

        let pickedTeam = getItemFromDictByValue(match.picks, "map", map).team;

        if (pickedTeam === match.firstTeam.name)
            return match.firstTeam.name;
        else if (pickedTeam === match.secondTeam.name)
            return match.secondTeam.name;
    }


    function toggleClass(id) { // функция toggle для селектора
        let temp = [];
        for (let i = 0; i < selectorCountryActive.length; ++i) {
            temp.push(false);
        }
        if (id !== "all") {
            temp[id] = !selectorCountryActive[id];
        }
        setSelectorCountryActive(temp);
    };


    function setCountryValue(id, value, src) {
        let tempCountries = [...valueStreams];
        tempCountries[id].country = value;
        tempCountries[id].flagPath = src;
        setValueStreams(tempCountries);
    }


    function checkStreamLink(link) {
        if (link === activeLink) {
            setActiveLink(null);
        } else {
            setActiveLink(link);
        }
    }


    function getTwitchChannel() {
        let idx = activeLink.lastIndexOf("/") + 1;
        return activeLink.substring(idx);
    }


    function generateStreamFields() {
        let content = [];

        for (let i = 0; i < valueStreams.length; ++i) {
            content.push(
                <div className="row_center_gap3" key={`streamFiled/${i}`}>
                    <div className="row_center_7">
                        <SearchableArraySelector issuer={"country"} styleMain={{ zIndex: valueStreams.length - i }} toggleClass={toggleClass} value={valueStreams[i].country} startValue={"Выберите страну"} selectorActive={selectorCountryActive[i]} data={countries} setDataValue={setCountryValue} itemKey={"countryRU"} srcKey={"flagPathMini"} index={i} type={"third"} />
                        <div className="text-field_third">
                            <input className="text-field_input" type="text" name="stream_name" id="stream_name" placeholder="Название" value={valueStreams[i].name} onChange={e => { setChangedTextInput(i, e.target.value, "name") }} />
                        </div>
                        <div className="text-field_third">
                            <input className="text-field_input" type="text" name="stream_link" id="stream_link" placeholder="Ссылка" value={valueStreams[i].link} onChange={e => { setChangedTextInput(i, e.target.value, "link") }} />
                        </div>
                    </div>
                    <div className="minus" onClick={() => removeStream(i, "valueStreams")}></div>
                </div>
            );
        }

        return content;
    }


    async function onStreamsEdited() {
        if ((streams === null || (streams !== null && streams.length === matchStreams.length)) && ipValue === match.ip && valueStreams.length === 0) {
            showNotification("Вы ничего не изменили", "neutral");
        } else if (valueStreams.some((stream, index) => !isLastFinished(index))) {
            showNotification("Вы не заполнили стрим", "warn");
        } else {
            try {
                let editedStreams = await request("POST", `editMatchStreams/${match.event}/${params.id}`, {
                    streams: streams !== null ? [...streams, ...valueStreams] : valueStreams,
                    ip: ipValue
                });

                setMatch({
                    ...match,
                    ip: ipValue
                });

                setMatchStreams(editedStreams.data);

                showNotification("Вы успешно изменили данные трансляций", "ok");
                setStreamsEditorActive(false);
            } catch (err) {
                if (err.response) {
                    showNotification(err.response.data.message, "warn");
                } else {
                    showNotification("Произошла непредвиденная ошибка", "warn");
                }
            }
        }
    }


    function onStreamsEdit() {
        setStreams(matchStreams);
        setStreamsEditorActive(true);
    }


    function atleastOneMapEnded() {
        return match.maps.some(map => map.status === "ended");
    }



    function getCurrentMap() {
        return match.maps.filter((map, index) => (map.status === "playing") || (map.status === "ended" && match.matchStatus === 1 && (index === (match.maps.length - 1) || match.maps[index + 1].status === "upcoming")));
    }


    return (
        <div>
            {allEvents !== null && match !== null && teams !== null ?
                <div style={{ display: "inline-flex", flexDirection: "column", alignItems: "center", gap: "50px", width: "100%" }}>
                    <div>
                        <MatchHeader match={match} isAdmin={isAdmin} teams={teams} matchId={params.id} allEvents={allEvents} setMatch={setMatch} />

                        <div className="match_info_upcoming">
                            <div>
                                <Description match={match} isAdmin={isAdmin} setMatch={setMatch} matchId={params.id} />

                                <div className="p_fixer">
                                    {match.maps.map((map, i) =>
                                        <MatchMap logoFirst={match.firstTeam.logo} nameFirst={match.firstTeam.name} logoSecond={match.secondTeam.logo} nameSecond={match.secondTeam.name} map={map} pickedBy={getByWhoPicked(map.mapName)} partType={match.partType} key={`${map.mapName}/${i}`} />
                                    )}
                                </div>
                            </div>

                            <div>
                                <div className="row_center_5px" style={{ marginBottom: "5px" }}>
                                    <p className="p_fixer">{match.matchStatus === 2 ? "Повтор" : "Просмотр"}</p>
                                    {isAdmin && match.matchStatus !== 2 ? <Editor size="14px" depth={2} onClick={() => { setValueStreams([]); setIpValue(match.ip); onStreamsEdit() }} /> : <></>}
                                </div>
                                <div className="match_info_upcoming_stream">
                                    {((match.matchStatus === 0) || (match.matchStatus === 1)) ? <p>Прямые трансляции</p> : null}
                                    {match.matchStatus === 2 && (matchStreams === null || matchStreams.length === 0) ? <p>Повторы отсутствуют</p> : match.matchStatus === 2 ? <p>Повторы</p> : null}
                                </div>

                                {((isParticipant || isAdmin) && match.matchStatus !== 2 && !isEmpty(match.ip, "str")) &&
                                    <div className="ip_match">
                                        {!ipMatch ?
                                            <p className="ip_hidden">IP скрыт</p>
                                            :
                                            <p className="ip_open" style={{ color: "white", fontFamily: "var(--text-medium-lcg)" }}>{match.ip}</p>
                                        }
                                        <div className="ip_block_images">
                                            <img className={!ipMatch ? "eye" : "eye eye_active"} src={!ipMatch ? "../../img/Eye.svg" : "../../img/scrollLog/howKilled/Attackerblind.svg"} onClick={() => ipMatch ? setActiveIpMatch(!ipMatch) : setActiveIpMatch(true)} alt="Показать IP-адрес" />
                                            <img src="../../img/Copy.svg" title="Копировать IP-адрес" style={{ cursor: "pointer" }} onClick={() => { showNotification("IP-адрес успешно скопирован", "ok"); navigator.clipboard.writeText(match.ip) }} alt="Копировать IP-адрес" />
                                            <a href={`steam://connect/${match.ip}`}>
                                                <img src="../../img/twitch_link.svg" alt="Ссылка на Twitch" />
                                            </a>

                                        </div>
                                    </div>
                                }

                                {matchStreams !== null ?
                                    matchStreams.map((stream) =>
                                        <Streams flagPath={stream.flagPath} name={stream.name} country={stream.country} viewers={stream.viewers} link={stream.link} checkStreamLink={checkStreamLink} key={stream.name} />
                                    )
                                    :
                                    <></>
                                }

                            </div>
                        </div>
                    </div>

                    <div style={{ display: "inline-flex", flexDirection: "column", alignItems: "center", gap: "30px", width: "100%" }}>
                        {activeLink !== null ?
                            activeLink.includes("twitch.tv") ?
                                <ReactTwitchEmbedVideo channel={getTwitchChannel()} layout="video" height={"382.79"} width={"648"} />
                                :
                                <ReactPlayer url={activeLink} height={"382.79px"} width={"648px"} />
                            :
                            <></>
                        }

                        {getCurrentMap().length !== 0 &&
                            <div className="scoreboard_block">
                                <p>Таблица</p>
                                <Scoreboard partType={match.partType} matchId={params.id} event={match.event} nameFirst={match.firstTeam.name} />
                            </div>
                        }

                        {getCurrentMap().length !== 0 &&
                            <div className="scroll_logs_block">
                                <p>Игровые события</p>
                                <ScrollLog matchId={params.id} event={match.event} />
                            </div>
                        }

                        {atleastOneMapEnded() &&
                            <div className="match_statistic_block">
                                <p>Статистика матча</p>
                                <Statistic firstTeam={match.firstTeam} secondTeam={match.secondTeam} maps={match.maps} partType={match.partType} />
                            </div>
                        }

                        {match.partType === "team" ?
                            <div>
                                <p className="p_fixer" style={{ marginBottom: "5px" }}>Составы команд</p>
                                <div className="col_center_gap10">
                                    <TeamRosters {...match.firstTeam} />
                                    <TeamRosters {...match.secondTeam} />
                                </div>
                            </div>
                            :
                            <></>
                        }
                    </div>
                </div>
                :
                <Preloader />
            }

            {
                isAdmin && match !== null ?
                    <Login active={streamsEditorActive} setActive={setStreamsEditorActive}>
                        <div className="header_splash_window">
                            <div className="logo_splash_window"></div>
                        </div>
                        <div className="info_text">
                            <p>Редактирование трансляций</p>
                        </div>
                        <div className="col_center_gap30">
                            {/* TODO: сделать такие проверки на высоту во всех местах, где есть селекторы*/}
                            <div className="inside scroll" style={{ paddingLeft: "8px", justifyContent: "flex-start", height: selectorCountryActive.some(active => active === true) ? "330px" : null, overflow: !selectorCountryActive.some(active => active === true) && matchStreams !== null && (valueStreams.length + matchStreams.length) <= 3 ? "hidden" : null, gap: "20px" }}>
                                <div className="col_center_gap10">
                                    <div className="text-field">
                                        <input className="text-field_input" type="text" name="ip" id="ip" placeholder="Введите IP сервера" style={{ color: ipValue === match.ip ? "var(--white70)" : "white", width: "453px" }} value={ipValue} onChange={(ev) => setIpValue(ev.target.value)} />
                                    </div>
                                    {streams !== null ?
                                        streams.map((stream, index) =>
                                            <div className="row_center_gap3" key={`addStream/${index}`}>
                                                <div className="row_center_7">
                                                    <NonSelectableSelector type={"third"} styleMain={null} text={stream.country} styleP={{ color: "var(--white70)" }} />
                                                    <NonSelectableSelector type={"third"} styleMain={null} text={stream.name} styleP={{ color: "var(--white70)" }} />
                                                    <NonSelectableSelector type={"third"} styleMain={null} text={stream.link} styleP={{ color: "var(--white70)" }} />
                                                </div>
                                                <div className="minus" onClick={() => removeStream(index, "matchStreams")}></div>
                                            </div>
                                        )
                                        :
                                        <></>
                                    }
                                    {generateStreamFields()}
                                </div>
                                <div className="add_stream" onClick={() => { addStream() }}>
                                    <p>Добавить трансляцию</p>
                                    <img src="../../img/Add.svg" alt="Плюс" />
                                </div>
                            </div>
                            <div className="full_grey_button">
                                <input type="submit" value="Подтвердить" onClick={() => { onStreamsEdited() }} />
                            </div>
                        </div>
                    </Login>
                    :
                    <></>
            }
        </div >
    )
}
export default Match;