import React from "react";
import { useRef, useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from "react-router-dom";
import FlagName from "../../components/FlagName/FlagName";
import Trophies from "../../components/Trophies/Trophies";
import PlayerTabs from "../../components/Tabs/PlayerTabs/PlayerTabs";
import Editor from "../../components/Editor/Editor";
import Login from "../../components/Login/Login";
import DefaultSelector from "../../components/Selector/DefaultSelector";
import SearchableSelector from "../../components/Selector/SearchableSelector";
import { fillSpaces, onImageUploaded, getStoredPlayerNick, showNotification, isNotInList, getItemFromDictByValue, getImage, getMonthName, resetRef } from "../../Utils/Utils";
import { applHeaders, request } from "../../Utils/MyAxios";
import { faceitAuth } from "../SocialAuth/FaceitAuth";
import { getClientDomain, getClientUrl } from "../../Utils/HostData";
import "./Player.css"
import "../../pages/Team/Team.css"
import '../../components/Editor/Editor.css';
import Preloader from "../../components/Preloader/Preloader";

function Player() {

    const params = useParams(); // параметр из ссылки(ник игрока)
    const navigate = useNavigate();

    // проверяем, владелец ли тот, кто зашел на аккаунт
    const [isOwner, seIsOwner] = useState((getStoredPlayerNick() !== null && getStoredPlayerNick() !== "null" && getStoredPlayerNick() !== "undefined" && getStoredPlayerNick() === params.id));

    const [social, setSocial] = useState(null);
    const [trophies, setTrophies] = useState(null);
    const [playerFlagName, setPlayerFlagName] = useState(null);
    const [playerImage, setPlayerImage] = useState(null);
    const [playerTeam, setPlayerTeam] = useState(null);

    const [isAdmin, setIsAdmin] = useState(false); // true false
    const [nick, setNick] = useState(params.id);

    const [matchesUpcoming, setMatchesUpcoming] = useState(null);
    const [matchesEnded, setMatchesEnded] = useState(null);

    const [leaveTeamActive, setLeaveTeamActive] = useState(true); //состояния модального окна для выхода из команды

    const [ongoingEvents, setOngoingEvents] = useState(null);
    const [endedEvents, setEndedEvents] = useState(null);

    const [lanEvents, setLanEvents] = useState(null);
    const [onlineEvents, setOnlineEvents] = useState(null);

    const [rosters, setRosters] = useState(null);
    const [curTeamDays, setCurTeamDays] = useState(null);
    const [allTeamsDays, setAllTeamsDays] = useState(null);


    async function getIsAdmin() {
        let resp = await request("GET", `/isAdmin/${getStoredPlayerNick()}`, {}, applHeaders);
        setIsAdmin(resp.data);
    }


    async function setTeam(teamData) {
        if (teamData.name !== "") {
            setPlayerTeam({ ...teamData, src: await getImage(teamData.name) });
            setLeaveTeamActive(false);
        } else {
            setPlayerTeam(teamData);
            setLeaveTeamActive(true);
        }
    }

    const [stats, setStats] = useState(null);


    async function setPlayerMatches(matchesData, setMatches) {
        setMatches(await Promise.all(matchesData.map(async event => ({
            ...event,
            matches: await Promise.all(event.matches.map(async match => ({
                ...match,
                leftTeamSrc: await getImage(match.leftTeam),
                rightTeamSrc: await getImage(match.rightTeam)
            })))
        }))));
    }


    async function setPlayerEvents(type, setEvents, eventData) {
        if (type !== "ended") {
            setEvents(await Promise.all(eventData.map(async event => ({
                ...event,
                logo: await getImage(event.name),
                participants: await Promise.all(event.participants.map(async part => ({
                    ...part,
                    src: await getImage(part.teamName)
                })))
            }))))
        } else {
            setEvents(await Promise.all(eventData.map(async event => ({
                ...event,
                logo: await getImage(event.name)
            }))))
        }
    }


    async function setPlayerAchievements(achievementsData, setAchievements) {
        setAchievements(await Promise.all(achievementsData.map(async event => ({
            ...event,
            logo: await getImage(event.name)
        }))));
    }


    async function setPlayerRosters(rostersData) {
        let sumDays = 0;
        for (let i = 0; i < rostersData.length; ++i) {
            let roster = rostersData[i];

            if (roster.period.includes("Настоящее время")) {
                setCurTeamDays(roster.dayDiff);
            }

            roster["teamLogo"] = await getImage(roster.team);
            roster.trophies = await Promise.all(roster.trophies.map(async trophy => ({
                ...trophy,
                src: await getImage(trophy.name, "/trophy")
            })));
            rostersData[i] = roster;
            sumDays += roster.dayDiff;
        }
        setRosters(rostersData);
        setAllTeamsDays(sumDays);
    }


    async function getPlayerImage() {
        setPlayerImage(await getImage(params.id));
    }


    async function setPlayerTrophies(trophies) {
        setTrophies(await Promise.all(trophies.map(async trophy => ({
            ...trophy,
            src: await getImage(trophy.name, "/trophy")
        }))));
    }


    async function getFullPlayer() {
        try {
            let response = await request("GET", `/getFullPlayer/${params.id}`, {}, applHeaders);
            if (response.data.isAdmin) {
                setNick(`${params.id} (Админ)`);
                setValueNick(`${params.id} (Админ)`);

                if (getStoredPlayerNick() === params.id) {
                    setIsAdmin(true);
                }
            }

            setPlayerMatches(response.data.matchesEnded, setMatchesEnded);
            setPlayerMatches(response.data.matchesUpcoming, setMatchesUpcoming);

            setPlayerAchievements(response.data.lanAchievements, setLanEvents);
            setPlayerAchievements(response.data.onlineAchievements, setOnlineEvents);

            setStats(response.data.stats);

            setPlayerRosters(response.data.playerRosters);

            setPlayerEvents("upcoming", setOngoingEvents, response.data.upcomingEvents);
            setPlayerEvents("ended", setEndedEvents, response.data.endedEvents);

            setSocial(response.data.social);

            setPlayerFlagName(response.data.flagName);

            setTeam(response.data.teamName);

            setAgePlayer(response.data.age);

            setPlayerTrophies(response.data.trophies);
        } catch (err) {
            navigate("/notfoundpage");
        }
    }


    useEffect(() => {
        getFullPlayer();
        getIsAdmin();
        getPlayerImage();
    }, []);


    const [nickEditorActive, setNickEditorActive] = useState(false); //состояния модального окна для редактирования ника игрока
    const [socialEditorActive, setSocialEditorActive] = useState(false); //состояния модального окна для редактирования соц сетей
    const [socialUnbindActive, setSocialUnbindActive] = useState(false); //состояния модального окна для отвязки соц сети


    const [mouseOutCard, setMouseOutCard] = useState(true); //Для ховера игрока
    const [mouseOnCard, setMouseOnCard] = useState(false); //Для ховера игрока

    const [socialToUnbind, setSocialToUnbind] = useState('');

    const [valueNick, setValueNick] = useState(nick); //Для селектора команды

    const nickRef = useRef(null);


    async function discordAuth() {
        let client_id = await request("GET", "/getDiscordClientID", {}, applHeaders).then(res => res.data.replaceAll("STRING", ""));
        const url = `https://discord.com/api/oauth2/authorize?client_id=${client_id}&redirect_uri=${getClientUrl()}/social-auth/discord/&response_type=token&scope=identify`
        window.open(url);
    }


    async function vkAuth() {
        // https://dev.vk.com/ru/api/access-token/authcode-flow-user#%D0%9E%D1%82%D0%BA%D1%80%D1%8B%D1%82%D0%B8%D0%B5%20%D0%B4%D0%B8%D0%B0%D0%BB%D0%BE%D0%B3%D0%B0%20%D0%B0%D0%B2%D1%82%D0%BE%D1%80%D0%B8%D0%B7%D0%B0%D1%86%D0%B8%D0%B8
        let client_id = await request("GET", "/getVKClientID", {}, applHeaders).then(res => res.data);
        const url = `https://oauth.vk.com/authorize?client_id=${client_id}&display=page&redirect_uri=${getClientUrl()}/social-auth/vk/&response_type=code&v=5.131`;
        window.open(url);
    }


    function steamAuth() {
        const url = `https://steamcommunity.com/openid/login?domain=${getClientDomain()}&openid.ns=http://specs.openid.net/auth/2.0&openid.mode=checkid_setup&openid.return_to=${getClientUrl()}/social-auth/steam/&openid.identity=http://specs.openid.net/auth/2.0/identifier_select&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select`;
        window.open(url);
    }


    function getSocialLowerLine(social) {
        switch (social.alt) {
            case "VK":
                return social.link.split("/")[3];
            case "Steam":
                return social.link.split("/")[4];
            case "Discord":
                return social.link;
            case "Faceit":
                return social.link.split("/")[5];
        }
    }


    function getIndex(arr, elem) {
        for (let i = 0; i < arr.length; ++i) {
            if (arr[i]["alt"] === elem) {
                return i;
            }
        }
    }


    async function unbindSocial() {
        let temp = social;
        const idx = getIndex(temp, socialToUnbind);
        temp[idx]["link"] = "";
        setSocial(temp);

        try {
            await request("POST", "/changeSocial", {
                player: params.id,
                link: "",
                social: socialToUnbind
            });
            showNotification(`Вы успешно отвязали ${socialToUnbind}`, "ok");
        } catch (err) {
            showNotification(err.response.data.message, "warn");
        }
    }


    async function onNickChanged(nick) {
        try {
            await request("POST", "/changeNick", {
                oldNick: params.id,
                newNick: nick
            });
            setValueNick(isAdmin ? (`${nick} (Админ)`) : nick);
            showNotification("Ник успешно изменён", "ok");
            navigate(`/player/${nick}`);
        } catch (err) {
            err.response.data.message ?
                showNotification("Пользователь с таким ником уже существует", "warn")
                :
                showNotification("Произошла неопознанная ошибка", "warn");
        }
    }


    function handleClick() {
        let newNick = nickRef.current.value;
        if (newNick === "") {
            showNotification("Вы не ввели ник", "warn");
        } else if (newNick === params.id) {
            showNotification("Новый ник не может совпадать со старым", "warn");
        } else {
            onNickChanged(newNick);
        }
        setNickEditorActive(!nickEditorActive);
    };


    async function handleImageUploaded(event) {
        let file = event.target.files[0];
        if (file !== null && file !== "null" && file !== "undefined" && file !== undefined) {
            if (file.size > 10485760) {
                showNotification("Размер фотографии не может быть больше 10мб", "warn");
            } else {
                try {
                    await onImageUploaded(file, "/players/", params.id);
                    setPlayerImage(URL.createObjectURL(file));
                    showNotification("Фотография успешно обновлена", "ok");
                } catch (err) {
                    showNotification("При обновлении фотографии произошла ошибка", "warn");
                }
            }
        }
    }


    function toggleOnMouseOver() {
        return (
            mouseOutCard ?
                <div>
                    {playerTeam !== null && playerTeam["name"] !== "" ?
                        <div className="player_team_logo"><img src={playerTeam["src"]} alt={playerTeam["name"]} /></div>
                        :
                        <></>
                    }
                    <div className="player">
                        {playerImage !== null ?
                            <div className="crop_player"><img src={playerImage} alt={params.id} /></div>
                            :
                            <></>
                        }
                    </div>
                </div>
                :
                <div className="img_hover_wrapper" onMouseOut={() => { setMouseOutCard(true); setMouseOnCard(false) }}>
                    <label htmlFor="file-input">
                        <img src="../../img/PlayerHovered.svg" alt="Выбор фотографии игрока" />
                    </label>
                    <input id="file-input" type="file" accept="image/*" onChange={handleImageUploaded} />
                </div>
        );
    }


    //----------Всё, что связано с селектором даты рождения--------------
    const [ageEditorActive, setAgeEditorActive] = useState(false); //Состояния модального окна для редактирования возраста
    const [dateSelectorActive, setDateSelectorActive] = useState(false); // Состояния селектора даты (открыт/закрыт календарь)
    const [dateSelected, setDateSelected] = useState('Укажите дату рождения'); // Здесь хранится выбраная дата
    const [valueStartDate, setValueStartDate] = useState('Укажите дату рождения'); // Это для даты выбранного матча
    const [agePlayer, setAgePlayer] = useState('Не указано');
    function toggleDate() { // Функция toggle для селектора даты
        setDateSelectorActive(!dateSelectorActive);
    };

    function getDate(date) {

        let day = parseInt(date.substring(0, 2));
        let month = parseInt(date.substring(3, 5)) - 1;
        let year = parseInt(date.substring(6, 10));

        const parsed = new Date();
        parsed.setFullYear(year);
        parsed.setDate(day);
        parsed.setMonth(month);

        return parsed;

    }

    function diffDate(date) { // функция для нахождения полных лет игрока
        // let diff = Math.floor(new Date() - getDate(date));
        // let day_hours = 1000 * 60 * 60 * 24;

        // let days = Math.floor(diff/day_hours);
        // let months = Math.floor(days/31);
        // let years = Math.floor(months/12);

        let curDate = new Date();
        let bdate = getDate(date);

        if (bdate.getFullYear() === 100) {
            return "Не указано";
        }

        let yearsDiff = curDate.getFullYear() - bdate.getFullYear();

        if ((curDate.getDate() - bdate.getDate() < 0) || (curDate.getMonth() - bdate.getMonth() < 0)) {
            yearsDiff -= 1;
        }

        if (yearsDiff % 100 >= 11 && yearsDiff % 100 <= 14) {
            yearsDiff = `${yearsDiff} лет`;
        } else if (yearsDiff % 10 === 1) {
            yearsDiff = `${yearsDiff} год`;
        } else if (yearsDiff % 10 === 2 || yearsDiff % 10 === 3 || yearsDiff % 10 === 4) {
            yearsDiff = `${yearsDiff} года`;
        } else {
            yearsDiff = `${yearsDiff} лет`;
        }

        return yearsDiff;
    }


    async function onDateSelected() {
        try {
            await request("POST", "/changeBDate", {
                bdate: dateSelected,
                player: params.id
            });
            setAgePlayer(diffDate(dateSelected));
            showNotification("Дата рождения успешно изменена", "ok");
        } catch (err) {
            showNotification(err.response.data.message, "warn");
        }
        setAgeEditorActive(!ageEditorActive);
    }


    function checkBirthday() {
        if (dateSelected === "Укажите дату рождения")
            showNotification("Введите дату", "warn");
        else
            onDateSelected();
    }

    //-------------------------------------------------------------------

    //----------Всё, что связано с селекторами страны и города-----------

    const [countries, setCountries] = useState(null);

    const [countrySelectorActive, setCountrySelectorActive] = useState(false); //Селектор страны
    const [citySelectorActive, setCitySelectorActive] = useState(false); // Селектор города

    const [citySelected, setCitySelected] = useState('Выберите город'); // Здесь хранится выбранный город

    const [countrySelected, setCountrySelected] = useState('Выберите страну'); // Здесь хранится выбранная страна


    async function getCountries() {
        let resp = await request("GET", "/country", {}, applHeaders);
        setCountries(resp.data);
    }


    useEffect(() => {
        getCountries();
    }, []);


    //-------------------------------------------------------------------

    //----------Всё, что связано с командой-----------

    const teamNameRef = useRef(null);
    const tagRef = useRef(null);


    const [leaveTeamWindowActive, setLeaveTeamWindowActive] = useState(false); //состояния модального окна для выхода из команды

    const [makeTeamActive, setMakeTeamActive] = useState(false); //состояния модального окна для создания команды


    async function onCreateTeam() {
        try {
            let resp = await request("POST", "/createTeam",
                {
                    name: teamNameRef.current.value,
                    tag: tagRef.current.value,
                    country: countrySelected,
                    city: citySelected === 'Выберите город' ? "" : citySelected,
                    cap: params.id
                });
            const temp = {
                name: resp.data.name,
                src: "../../img/teams_logo/NoLogo.svg"
            };

            setLeaveTeamActive(false);
            setPlayerTeam(temp);

            let curDate = new Date();
            const newTeam = {
                dayDiff: 0,
                period: `${getMonthName(curDate.getMonth() + 1)} ${curDate.getFullYear()} - Настоящее время`,
                team: resp.data.name,
                teamLogo: "../../img/teams_logo/NoLogo.svg",
                trophies: []
            }

            setRosters([newTeam].concat(rosters));
            setCurTeamDays(0);

            showNotification("Команда успешно создана", "ok");

            window.open(`/team/${resp.data.name}`);
        } catch (err) {
            showNotification(err.response.data.message, "warn");
        }
        setMakeTeamActive(!makeTeamActive);
    }


    function checkTeam() {
        if (teamNameRef.current.value === ""
            || teamNameRef.current.value === null
            || teamNameRef.current.value === undefined)
            showNotification("Вы не ввели название команды", "warn");
        else if (teamNameRef.current.value.length > 15)
            showNotification("Недопустимое название команды (больше 15 символов)", "warn")
        else if (! /^[\u0400-\u04FFA-Za-z0-9@_ !]+$/.test(teamNameRef.current.value))
            showNotification("Название команды содержит недопустимые символы\n(Из специальных символов разрешены лишь @ _ !)", "warn")
        else if (tagRef.current.value === ""
            || tagRef.current.value === null
            || tagRef.current.value === undefined)
            showNotification("Вы не ввели тэг команды", "warn");
        else if (tagRef.current.value.length > 8)
            showNotification("Недопустимый тэг команды (больше 8 символов)", "warn")
        else if (! /^[\u0400-\u04FFA-Za-z0-9@_!']+$/.test(tagRef.current.value))
            showNotification("Тег команды содержит недопустимые символы, либо пробелы\n(Из специальных символов разрешены лишь @ _ ! ')", "warn")
        else if (countrySelected === "Выберите страну")
            showNotification("Вы не выбрали страну", "warn");
        else if (isNotInList(countries, "countryRU", countrySelected))
            showNotification("Такой страны нет в списке", "warn");
        else if (citySelected !== "" && citySelected !== 'Выберите город' && isNotInList(getItemFromDictByValue(countries, "countryRU", countrySelected).cities, "", citySelected))
            showNotification("Такого города нет в списке", "warn");
        else
            onCreateTeam();
    }


    function getExitDate() {
        let date = new Date();
        let month;
        switch (date.getMonth()) {
            case 0:
                month = "Январь"; break;
            case 1:
                month = "Февраль"; break;
            case 2:
                month = "Март"; break;
            case 3:
                month = "Апрель"; break;
            case 4:
                month = "Май"; break;
            case 5:
                month = "Июнь"; break;
            case 6:
                month = "Июль"; break;
            case 7:
                month = "Август"; break;
            case 8:
                month = "Сентябрь"; break;
            case 9:
                month = "Октябрь"; break;
            case 10:
                month = "Ноябрь"; break;
            case 11:
                month = "Декабрь"; break;
        }

        return `${month} ${date.getFullYear()}`;
    }


    function onAgeEdit() {
        setDateSelected(valueStartDate);
        setDateSelectorActive(false);
        setAgeEditorActive(true);
    }


    async function onLeftTeam() {
        try {
            await request("POST", "/leftTeam",
                {
                    nick: params.id,
                    team: playerTeam["name"],
                    isKick: false
                });
            setLeaveTeamActive(!leaveTeamActive);
            setPlayerTeam(null);
            setRosters(rosters.map(roster => ({
                ...roster,
                period: roster.period.includes("Настоящее время") ? roster.period.replace("Настоящее время", getExitDate()) : roster.period
            })));
            showNotification("Вы успешно покинули команду", "ok");
        } catch (err) {
            showNotification(err.response.data.message, "warn");
        }
        setLeaveTeamWindowActive(!leaveTeamWindowActive);
    }


    function onOpenCreateTeamWindow() {
        setCountrySelectorActive(false);
        setCountrySelected("Выберите страну");
        setCitySelected("Выберите город");
        setCitySelectorActive(false);
        resetRef(teamNameRef);
        resetRef(tagRef);
        setMakeTeamActive(true);
    }

    //--------------------------------------------------------------------

    return (
        <div>
            {playerTeam !== null && playerImage !== null && playerFlagName !== null && social !== null && trophies !== null && stats !== null ?
                <>
                    <div className="user_back">
                        <div className="player_card_wrapper" onMouseOut={() => { setMouseOutCard(true); setMouseOnCard(false) }} onMouseOver={() => { setMouseOutCard(false); setMouseOnCard(true) }}>
                            {isAdmin ? toggleOnMouseOver() :
                                <div>
                                    {playerTeam["name"] !== "" ?
                                        <div className="player_team_logo"><img src={playerTeam["src"]} alt={playerTeam["name"]} /></div>
                                        :
                                        <></>
                                    }
                                    <div className="player">
                                        <div className="crop_player"><img src={playerImage} alt={params.id} /></div>
                                    </div>
                                </div>
                            }
                        </div>
                        <div className="player_info">
                            <div className="player_nick">
                                <p>{valueNick}</p>
                                {isAdmin ? <Editor size="18px" depth={2} onClick={() => setNickEditorActive(true)} />
                                    : <></>}
                            </div>
                            <FlagName flagPath={playerFlagName.flagPath} country={playerFlagName.country} name={playerFlagName.name} height='12px' />
                            <div className="devider_info">
                                <div className="devider_info_line">
                                    <div className="row_center_5px">
                                        <span>Возраст</span>
                                        {((isOwner && agePlayer === "Не указано") || isAdmin) ? <Editor size="12px" depth={2} onClick={() => onAgeEdit()} />
                                            : <></>}
                                    </div>
                                    <p>{agePlayer}</p>
                                </div>
                                <div className="devider_subline"></div>
                            </div>
                            <div className="devider_info">
                                <div className="devider_info_line">
                                    <div className="row_center_5px">
                                        <span>Текущая команда</span>
                                        {isOwner && !leaveTeamActive ?
                                            <div className="editor" style={{ width: "16px", height: "16px" }} onClick={() => setLeaveTeamWindowActive(true)}>
                                                <img src="../../img/Cross.svg" alt="Покинуть команду" />
                                            </div>
                                            : isOwner ?
                                                <div className="editor" style={{ width: "17px", height: "17px" }} onClick={() => { onOpenCreateTeamWindow(); }}>
                                                    <img src="../../img/Add.svg" alt="Создать команду" />
                                                </div>
                                                :
                                                <></>
                                        }

                                    </div>

                                    {leaveTeamActive ? <p>Отсутствует</p> :
                                        <Link to={`/team/${fillSpaces(playerTeam["name"])}`} style={{ textDecoration: "none" }}>
                                            <div className="devider_team">
                                                <div className="devider_team_logo"><img src={playerTeam["src"]} alt={playerTeam["name"]} /></div>
                                                <p>{playerTeam["name"]}</p>
                                            </div>
                                        </Link>
                                    }
                                </div>
                                <div className="devider_subline"></div>
                            </div>
                            <div className="devider_info">
                                <div className="devider_info_line">
                                    <div className="row_center_5px">
                                        <span>Социальные сети</span>
                                        {isAdmin || isOwner ? <Editor size="12px" depth={2} onClick={() => setSocialEditorActive(true)} />
                                            : <></>}

                                    </div>
                                    <div className="social_media">
                                        {social.map((item) =>
                                            item.link !== "" ? item.alt !== "Discord" ? <a href={item.link} target="_blank" rel="noopener noreferrer" key={item.alt}><img className={item.color === "white" ? 'active_elem' : 'active_colored'} src={item.src} alt={item.alt} /></a> :
                                                <img className={item.color === "white" ? 'active_elem' : 'active_colored'} src={item.src} alt={item.alt} title="Скопировать ник" key={item.alt} onClick={() => { navigator.clipboard.writeText(item.link); showNotification("Текст скопирован", "ok") }} /> :
                                                <img className={item.color === "white" ? 'inactive_elem' : 'inactive_colored'} src={item.src} alt={item.alt} key={item.alt} />
                                        )}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* div, разделяющий плашку игрока и трофеи */}
                    <div className="devider_line"></div>

                    {/* Трофеи */}
                    <Trophies items={trophies} />

                    {/* Табы игрока (Статистика, матчи, турниры ... ) */}
                    <PlayerTabs
                        stat={stats}
                        rosters={rosters}
                        matches_upcoming={matchesUpcoming}
                        matches_ended={matchesEnded}
                        ongoing_events={ongoingEvents}
                        ended_events={endedEvents}
                        lan_events={lanEvents}
                        online_events={onlineEvents}
                        nick={params.id}
                        curTeamDays={curTeamDays}
                        allTeamsDays={allTeamsDays}
                    />
                </>
                :
                <Preloader />
            }

            {/* Модальное окно "Редактирование ника" */}
            {isAdmin ?
                <Login active={nickEditorActive} setActive={setNickEditorActive}>
                    <div className="header_splash_window">
                        <div className="logo_splash_window"></div>
                    </div>
                    <div className="info_text">
                        <p>Укажите ник игрока</p>
                    </div>
                    <div className="col_center_gap30">
                        <div className="text-field">
                            <input className="text-field_input" type="text" name="nick" placeholder="Введите ник игрока" ref={nickRef} />
                        </div>
                        <div className="full_grey_button">
                            <input type="submit" value="Сохранить" onClick={handleClick} />
                        </div>
                    </div>
                </Login>
                :
                <></>
            }

            {/* Модальное окно "Редактирование социальных сетей" */}
            {isAdmin || isOwner ?
                <Login active={socialEditorActive} setActive={setSocialEditorActive}>
                    <div className="header_splash_window">
                        <div className="logo_splash_window"></div>
                    </div>
                    <div className="info_text">
                        <p>Управление социальными сетями</p>
                    </div>
                    {social !== null ?
                        social.map((item, i) =>
                            <div key={`socialEditor/${i}`}>
                                <div className="social_row">
                                    <div className="logo_social_wrap">
                                        <div className={item.color === "white" ? "social_logo" : "social_logo colored_to_white"}><img src={item.src} alt={item.alt} /></div>
                                        <div className="social_wrapper">
                                            <span>{item.alt}</span>
                                            {item.link === "" ? <p>Не указано</p> :
                                                <p>{getSocialLowerLine(item)}</p>
                                            }
                                        </div>
                                    </div>
                                    {isAdmin && item.link !== "" ?
                                        <span className="unbind" onClick={() => { setSocialUnbindActive(true); setSocialEditorActive(false); setSocialToUnbind(item.alt) }}>Отвязать</span> :
                                        null
                                    }
                                    {isOwner && item.link === "" ? <span className="unbind" onClick={() => { item.alt === "Discord" ? discordAuth() : item.alt === "VK" ? vkAuth() : item.alt === "Steam" ? steamAuth() : faceitAuth(); setSocialEditorActive(false) }}>Подключить</span> :
                                        null
                                    }
                                </div>

                                <div className="social_devider"></div>
                            </div>
                        )
                        :
                        <></>
                    }
                    <div className="full_grey_button" >
                        <input type="submit" value="Сохранить" onClick={() => socialEditorActive ? setSocialEditorActive(!socialEditorActive) : null} />
                    </div>
                </Login>
                :
                <></>
            }


            {/* Модальное окно "Вы уверены, что хотите отвзяать соц. сеть?" */}
            {isAdmin ?
                <Login active={socialUnbindActive} setActive={setSocialUnbindActive}>
                    <div className="header_splash_window">
                        <div className="logo_splash_window"></div>
                    </div>
                    <div className="info_text">
                        <p>Вы уверены, что хотите отвязать {socialToUnbind} игрока {params.id}?</p>
                    </div>
                    <div className="small_buttons_wrapper">
                        <div className="small_dark_button">
                            <input type="submit" value="Нет" onClick={() => socialUnbindActive ? setSocialUnbindActive(!socialUnbindActive) : null} />
                        </div>
                        <div className="small_grey_button">
                            <input type="submit" value="Да" onClick={() => { setSocialUnbindActive(false); unbindSocial() }} />
                        </div>
                    </div>
                </Login>
                :
                <></>
            }

            {/* Модальное окно "Редактирование возраста" */}
            {((isOwner && agePlayer === "Не указано") || isAdmin) ?
                <Login active={ageEditorActive} setActive={setAgeEditorActive}>
                    <div className="header_splash_window">
                        <div className="logo_splash_window"></div>
                    </div>

                    <div className="info_text">
                        <p>Укажите дату рождения</p>
                    </div>
                    <div className="col_center_gap30">
                        <div className="inside scroll" style={{ height: dateSelectorActive ? "400px" : null, overflow: !dateSelectorActive ? "hidden" : null }}>
                            <DefaultSelector issuer={"datePicker"} type={"datePicker"} styleMain={null} toggleClass={toggleDate} value={dateSelected} startValue={valueStartDate} setValue={setDateSelected} selectorActive={dateSelectorActive} minDate={getDate("05.01.1990")} maxDate={new Date()} />
                        </div>
                        <div className="full_grey_button" >
                            <input type="submit" value="Подвердить" onClick={() => checkBirthday()} />
                        </div>
                    </div>
                </Login>
                :
                <></>
            }

            {/* Модальное окно "Вы уверены, что хотите покинуть команду?" */}
            {isOwner && !leaveTeamActive ?
                <Login active={leaveTeamWindowActive} setActive={setLeaveTeamWindowActive}>
                    <div className="header_splash_window">
                        <div className="logo_splash_window"></div>
                    </div>
                    <div className="info_text">
                        {playerTeam !== null ?
                            <p>Вы уверены, что хотите покинуть команду {playerTeam["name"]}?</p>
                            :
                            <></>
                        }
                    </div>
                    <div className="small_buttons_wrapper">
                        <div className="small_dark_button">
                            <input type="submit" value="Нет" onClick={() => leaveTeamWindowActive ? setLeaveTeamWindowActive(!leaveTeamWindowActive) : null} />
                        </div>
                        <div className="small_grey_button">
                            <input type="submit" value="Да" onClick={() => leaveTeamWindowActive ? onLeftTeam() : null} />
                        </div>
                    </div>
                </Login>
                :
                <></>
            }

            {/* Модально окно "Создание команды" */}
            {isOwner && leaveTeamActive ?
                <Login active={makeTeamActive} setActive={setMakeTeamActive}>
                    <div className="header_splash_window">
                        <div className="logo_splash_window"></div>
                    </div>
                    <div className="info_text">
                        <p>Укажите информацию о команде</p>
                    </div>
                    <div className="col_center_gap30">

                        <div className="col_center_gap10">
                            <div className="text-field">
                                <input className="text-field_input" style={{ width: "430px" }} type="text" name="teamName" id="teamName" placeholder="Название. Максимум 15 символов" ref={teamNameRef} />
                            </div>
                            <div className="text-field">
                                <input className="text-field_input" style={{ width: "430px" }} type="text" name="tag" id="tag" placeholder="Тег. Максимум 8 символов" ref={tagRef} />
                            </div>
                            <div className="row_center_6 display-on-start">
                                <SearchableSelector issuer={"country"} type={"half"} styleMain={null} styleItems={null} setSelectorActive={setCountrySelectorActive} value={countrySelected} startValue={"Выберите страну"} selectorActive={countrySelectorActive} data={countries} setValue={setCountrySelected} setCity={setCitySelected} itemKey={"countryRU"} srcKey={"flagPathMini"} />
                                {countrySelected !== "Выберите страну" && getItemFromDictByValue(countries, "countryRU", countrySelected) !== undefined ?
                                    <SearchableSelector issuer={"city"} type={"half"} styleMain={null} styleItems={null} setSelectorActive={setCitySelectorActive} value={citySelected} startValue={"Выберите город"} selectorActive={citySelectorActive} data={getItemFromDictByValue(countries, "countryRU", countrySelected).cities} setValue={setCitySelected} />
                                    :
                                    <></>
                                }
                            </div>
                        </div>
                        <div className="small_buttons_wrapper">
                            <div className="small_dark_button" style={{ width: "122px", height: "48px" }} >
                                <input type="submit" value="Отмена" style={{ width: "122px", height: "48px" }} onClick={() => makeTeamActive ? setMakeTeamActive(!makeTeamActive) : null} />
                            </div>
                            <div className="small_grey_button" style={{ width: "122px", height: "48px" }}>
                                <input type="submit" value="Создать" style={{ width: "122px", height: "48px" }} onClick={() => checkTeam()} />
                            </div>
                        </div>
                    </div>
                </Login>
                :
                <></>
            }
        </div>
    );
}

export default Player;