import React, { useState, useEffect } from "react";
import { Routes, Route, NavLink, Navigate, useParams, useNavigate } from 'react-router-dom'
import FlagName from "../../components/FlagName/FlagName";
import EventInfo from "../../components/EventMaker/EventInfo/EventInfo";
import MatchesGenerator from "../../components/MatchHelper/MatchesGenerator";
import EventResults from "../../components/EventMaker/EventResults/EventResults";
import Login from "../../components/Login/Login";
import Editor from "../../components/Editor/Editor";
import DefaultSelector from "../../components/Selector/DefaultSelector";
import SearchableArraySelector from "../../components/Selector/SearchableArraySelector";
import SearchableSelector from "../../components/Selector/SearchableSelector";
import NonSelectableSelector from "../../components/Selector/NonSelectableSelector";
import FileLoader from "../../components/FileLoader/FileLoader";
import "./Event.css";
import '../../components/Tabs/PlayerTabs/PlayerTabs.css';
import { applHeaders, request } from "../../Utils/MyAxios";
import { toggleOffSelectors, getItemFromDictByValue, getStoredPlayerNick, showNotification, indexOf, genMaxDate, parseDateString, isNotInList, getMatchesWithImg, getImage, onImageUploaded, getTeamSrc, isEmpty, getEventDate } from "../../Utils/Utils";
import Timer from "../../components/Timer/Timer";
import Preloader from "../../components/Preloader/Preloader";

function Event() {
    const [isAdmin, setIsAdmin] = useState(false);
    const [isCap, setIsCap] = useState(false); //капитан ли смотрит

    const params = useParams();
    const navigate = useNavigate();

    const [registrationInfoEditorActive, setRegistrationInfoEditorActive] = useState(false);

    const [tournament, setTournament] = useState(null);

    const [eventMapPool, setEventMapPool] = useState(null);

    const [eventMVP, setEventMVP] = useState("");

    const [part_header, setPartHeader] = useState("Команды");

    const [ongoing_matches, setOngoingMatches] = useState(null);

    const [upcoming_matches, setUpcomingMatches] = useState(null);

    const [results, setResults] = useState(null);

    const [activeJoinTourWindow, setJoinTourWindowActive] = useState(false); // окно уточнения для регистрации на турик
    const [activeLeaveTourWindow, setLeaveTourWindowActive] = useState(false); // окно уточнения "покидания" турика

    const [activeTour, setActiveTour] = useState(false); // переменная, отвечающая за то зарегана ли команда на турик
    const [activeEditTeamWindow, setActiveEditTeamWindow] = useState(false); // окно изменения состава на турик

    const [dateSelected, setDateSelected] = useState('Выберите дату начала');
    const [dateEndSelected, setDateEndSelected] = useState('Выберите дату окончания');

    const [dateSelectorActive, setDateSelectorActive] = useState(false);
    const [dateEndSelectorActive, setDateEndSelectorActive] = useState(false);

    const [citySelected, setCitySelected] = useState('Выберите город');

    const [countrySelected, setCountrySelected] = useState('');

    const [countrySelectorActive, setCountrySelectorActive] = useState(false);
    const [citySelectorActive, setCitySelectorActive] = useState(false);

    const [selectedPrize, setSelectedPrize] = useState('');
    const [selectedFee, setSelectedFee] = useState('');

    const [selectedFormat, setSelectedFormat] = useState('Выберите формат');
    const [formatSelectorActive, setFormatSelectorActive] = useState(false);

    const [selectedType, setSelectedType] = useState('Выберите тип');
    const [typeSelectorActive, setTypeSelectorActive] = useState(false);
    const [deleteEventActive, setDeleteEventActive] = useState(false);

    const [editOngoingActive, setEditOngoingActive] = useState(false);

    const [valueLogoPath, setValueLogoPath] = useState({ path: "Укажите путь до логотипа", file: null });
    const [selectedLogoPath, setSelectedLogoPath] = useState({ path: "Укажите путь до логотипа", file: null });

    const [valueHeaderPath, setValueHeaderPath] = useState({ path: "Укажите путь до шапки", file: null });
    const [selectedHeaderPath, setSelectedHeaderPath] = useState({ path: "Укажите путь до шапки", file: null });

    const [valueTrophyPath, setValueTrophyPath] = useState({ path: "Укажите путь до трофея", file: null });
    const [selectedTrophyPath, setSelectedTrophyPath] = useState({ path: "Укажите путь до трофея", file: null });

    const [valueMvpPath, setValueMvpPath] = useState({ path: "Укажите путь до MVP", file: null });
    const [selectedMvpPath, setSelectedMvpPath] = useState({ path: "Укажите путь до MVP", file: null });

    const [selectedName, setSelectedName] = useState('');

    const [countries, setCountries] = useState(null);

    const [players, setPlayers] = useState(null);

    const [playerTeam, setPlayerTeam] = useState(null);

    const [choosedPlayers, setChoosedPlayers] = useState(['Выберите игрока', 'Выберите игрока']);


    const [playersActive, setPlayersActive] = useState(null); // состояния игроков - выбран ли игрок(чтоб блокировать его)
    const [selectorActive, setSelectorActive] = useState([false, false]); // состояния селектора
    const [valuePlayer, setValuePlayer] = useState(['Выберите игрока', 'Выберите игрока']); //Для селектора команды


    function toggleOff(idxArr) {
        let toggleDict = {
            0: setDateSelectorActive,
            1: setDateEndSelectorActive,
            2: setCountrySelectorActive,
            3: setCitySelectorActive,
            4: setFormatSelectorActive,
            5: setTypeSelectorActive
        }

        toggleOffSelectors(toggleDict, idxArr);
    }


    async function getPrizePlaceSrc(teamName) {
        if (teamName !== "" && teamName !== null) {
            return await getImage(teamName);
        }
        return "";
    }


    async function getTournament(tournament) {
        setTournament({
            ...tournament,
            eventSrc: await getImage(tournament.event),
            headerSrc: await getImage(tournament.event, "/header"),
            participants: await Promise.all(tournament.participants.map(async place => ({
                ...place,
                logo: await getImage(place.teamName),
                teamSrc: await getTeamSrc(place.team, tournament.partType)
            }))),
            prizePlaces: await Promise.all(tournament.prizePlaces.map(async place => ({
                ...place,
                logo: await getPrizePlaceSrc(place.teamName),
                teamSrc: await getTeamSrc(place.team, tournament.partType)
            }))),
            teamsPlayers: await Promise.all(tournament.teamsPlayers.map(async player => ({
                ...player,
                src: await getImage(player.nick),
                teamSrc: await getImage(player.team)
            })))
        });

        setPartHeader(tournament.partType === "team" ? "Команды" : "Участники")
    }


    async function getOngoingMatches(ongoingMatches) {
        setOngoingMatches(await getMatchesWithImg(ongoingMatches));
    }


    async function getUpcomingMatches(upcomingMatches) {
        setUpcomingMatches(
            await Promise.all(upcomingMatches.map(async day => ({
                ...day,
                matches: await getMatchesWithImg(day.matches)
            })))
        );
    }


    async function getResults(results) {
        setResults(await Promise.all(results.map(async day => ({
            ...day,
            matches: await getMatchesWithImg(day.matches)
        }))))
    }


    async function getPlayers(players) {
        setPlayers(await Promise.all(players.map(async player => ({
            ...player,
            src: await getImage(player.name)
        }))))
    }


    async function getChosenPlayers(playerTeam, participants, players) {
        let chP = participants.filter(part => (part.teamName === playerTeam))[0];

        let choosedPlayers;
        if (chP === undefined || chP.chosenPlayers === null || chP.chosenPlayers[0] === null) {
            choosedPlayers = [getStoredPlayerNick(), "Выберите игрока"];
        } else {
            choosedPlayers = chP.chosenPlayers;
        }

        let tempPlayersActive = Array(players.length).fill(false);
        choosedPlayers.map((player) => {
            let idx = indexOf(player, players, "name");
            if (idx !== undefined) {
                tempPlayersActive[idx] = true;
            }
        })

        setValuePlayer(choosedPlayers)
        setChoosedPlayers(choosedPlayers);
        setPlayersActive(tempPlayersActive);
    }


    async function getPlayerTeam(playerTeam_, tournament_) {
        setPlayerTeam({
            ...playerTeam_,
            status: playerTeam_.status === "-" ? "await" : playerTeam_.status,
            logo: await getImage(playerTeam_.teamName),
            teamSrc: await getTeamSrc(playerTeam_.team, tournament_.partType)
        });
    }


    async function getFullEvent() {
        try {
            let event = await request("GET", `/getFullEvent/${params.id}/${getStoredPlayerNick()}`, {}, applHeaders);

            getTournament(event.data.tournament);

            setEventMVP(event.data.mvp);

            getOngoingMatches(event.data.ongoingMatches);
            getUpcomingMatches(event.data.upcomingMatches);
            getResults(event.data.results);
            getPlayers(event.data.players);
            if (event.data.playerTeam !== null) {
                getChosenPlayers(event.data.playerTeam.teamName, event.data.tournament.participants, event.data.players);
                getPlayerTeam(event.data.playerTeam, event.data.tournament);
            }

            setIsAdmin(event.data.isAdmin);
            setEventMapPool(event.data.eventMapPool);
            setActiveTour(event.data.activeTour);
            setIsCap(event.data.isCap);
            setCountries(event.data.countries);
        } catch (err) {
            if (err.response !== undefined && err.response.data.message === "Такого турнира не существует") {
                navigate("/notfoundpage");
            } else {
                showNotification(err.response.data.message, "warn");
            }
        }
    }


    useEffect(() => {
        getFullEvent()
    }, []);


    function handleImageUploaded(event, setSelectedFile) {
        let file = event.target.files[0];
        if (file !== null && file !== "null" && file !== "undefined" && file !== undefined) {
            setSelectedFile({ path: file.name, file: file });
        }
    }


    function drawOngoing() {
        return (
            <div>
                <ul className="event_tabs">
                    <li className="tab_link" key="EventTabs Информация">
                        <div className="row_center_5px">
                            <NavLink to="info" style={({ isActive }) => ({  // если активна, то текст белый
                                color: isActive ? 'var(--text-01)' : 'var(--text-02)'
                            })}>
                                Информация
                            </NavLink>
                            {isAdmin ? <Editor size="14px" depth={2} onClick={() => { setEditOngoingActive(true); setOngoingEventInfo() }} /> : <></>}
                        </div>
                    </li>
                    <li className="tab_link" key="EventTabs Матчи">
                        <NavLink to="matches" style={({ isActive }) => ({  // если активна, то текст белый
                            color: isActive ? 'var(--text-01)' : 'var(--text-02)'
                        })}>
                            Матчи
                        </NavLink>
                    </li>
                    <li className="tab_link" key="EventTabs Результаты">
                        <NavLink to="results" style={({ isActive }) => ({  // если активна, то текст белый
                            color: isActive ? 'var(--text-01)' : 'var(--text-02)'
                        })}>
                            Результаты
                        </NavLink>
                    </li>
                </ul>
                <Routes>
                    <Route index element={<Navigate replace to={`/event/${params.id}/info`} />} />
                    <Route path="info" element={<EventInfo part_header={part_header} status="ongoing" isAdmin={isAdmin} tournament={tournament} setTournament={setTournament} eventMapPool={eventMapPool} mvp={eventMVP} setEventMVP={setEventMVP} />} />
                    <Route path="matches" element={<MatchesGenerator ongoing_matches={ongoing_matches} upcoming_matches={upcoming_matches} isAdmin={isAdmin} part={tournament.participants} event={[{ name: tournament.event, eventSrc: tournament.eventSrc }]} />} />
                    <Route path="results" element={<EventResults results={results} />} />
                </Routes>
            </div>
        );
    }


    function drawEnded() {
        return (
            <div>
                <ul className="event_tabs">
                    <li className="tab_link" key="EventTabs Информация">
                        <NavLink to="info" style={({ isActive }) => ({  // если активна, то текст белый
                            color: isActive ? 'var(--text-01)' : 'var(--text-02)'
                        })}>
                            Информация
                        </NavLink>
                    </li>
                    <li className="tab_link" key="EventTabs Результаты">
                        <NavLink to="results" style={({ isActive }) => ({  // если активна, то текст белый
                            color: isActive ? 'var(--text-01)' : 'var(--text-02)'
                        })}>
                            Результаты
                        </NavLink>
                    </li>
                </ul>
                <Routes>
                    <Route index element={<Navigate replace to={`/event/${params.id}/info`} />} />
                    <Route path="info" element={<EventInfo part_header={part_header} status="ended" isAdmin={isAdmin} tournament={tournament} eventMapPool={eventMapPool} />} />
                    <Route path="results" element={<EventResults results={results} />} />
                </Routes>
            </div>
        );
    }


    function setOngoingEventInfo() {
        setSelectedName(tournament.event);

        setValueLogoPath({ path: tournament.eventFile, file: null });
        setSelectedLogoPath({ path: tournament.eventFile, file: null });

        setValueHeaderPath({ path: tournament.headerFile, file: null });
        setSelectedHeaderPath({ path: tournament.headerFile, file: null });

        setValueTrophyPath({ path: tournament.trophyFile, file: null });
        setSelectedTrophyPath({ path: tournament.trophyFile, file: null });

        setValueMvpPath({ path: tournament.mvpFile, file: null });
        setSelectedMvpPath({ path: tournament.mvpFile, file: null });

        setSelectedPrize(tournament.prize);
    }


    function setEventInfo() {
        let splitDate = tournament.date.split(" - ");

        setDateSelected(splitDate[0]);
        setDateEndSelected(splitDate[1]);
        setDateSelectorActive(false);
        setDateEndSelectorActive(false);

        setCountrySelected(tournament.country);
        setCountrySelectorActive(false);

        setCitySelected(tournament.city);
        setCitySelectorActive(false);

        setOngoingEventInfo();

        setSelectedFormat(tournament.format);
        setFormatSelectorActive(false);

        setSelectedType(tournament.type);
        setTypeSelectorActive(false);

        setSelectedFee(tournament.fee);
    }


    async function onRegistrationEvent() {
        let splitDate = tournament.date.split(" - ");

        if (splitDate[0] === dateSelected && splitDate[1] === dateEndSelected && countrySelected === tournament.country &&
            citySelected === tournament.city && selectedPrize === tournament.prize && selectedFee === tournament.fee &&
            selectedFormat === tournament.format && selectedType === tournament.type &&
            valueLogoPath.path === selectedLogoPath.path && valueHeaderPath.path === selectedHeaderPath.path &&
            valueTrophyPath.path === selectedTrophyPath.path && valueMvpPath.path === selectedMvpPath.path &&
            selectedName === tournament.event) {
            showNotification("Вы ничего не изменили", "neutral");
        } else if (dateEndSelected === "Выберите дату окончания") {
            showNotification("Вы не указали дату окончания", "warn");
        } else if (isNotInList(countries, "countryRU", countrySelected)) {
            showNotification("Вы указали несуществующую страну", "warn");
        } else if (citySelected !== "" && citySelected !== "Выберите город" && isNotInList(getItemFromDictByValue(countries, "countryRU", countrySelected).cities, "", citySelected)) {
            showNotification("Вы указали несуществующий город", "warn");
        } else if (isEmpty(selectedPrize, "str")) {
            showNotification("Вы не указали приз", "warn");
        } else if (isEmpty(selectedFee, "str")) {
            showNotification("Вы не указали взнос", "warn");
        } else if (isEmpty(selectedName, "str")) {
            showNotification("Вы не указали название", "warn");
        } else if (selectedName.includes("-")) {
            showNotification('Название не должно содержать "-"', "warn");
        } else {
            let editedEvent = {
                ...tournament,
                event: selectedName,
                country: countrySelected,
                city: citySelected,
                date: `${dateSelected} - ${dateEndSelected}`,
                prize: selectedPrize,
                fee: selectedFee,
                type: selectedType,
                format: selectedFormat,
                eventFile: selectedLogoPath.path,
                headerFile: selectedHeaderPath.path,
                headerSrc: valueHeaderPath.path !== selectedHeaderPath.path ? URL.createObjectURL(selectedHeaderPath.file) : tournament.headerSrc,
                trophyFile: selectedTrophyPath.path,
                mvpFile: selectedMvpPath.path
            }

            try {
                await request("POST", `editUpcomingEvent/${tournament.event}`, editedEvent);

                uploadEventImages();

                setTournament(editedEvent);
                showNotification("Турнир успешно изменён", "ok");
                setRegistrationInfoEditorActive(false);
                navigate(`/event/${selectedName.replaceAll(" ", "-")}/info`)
            } catch (err) {
                showNotification(err.response.data.message, "warn");
            }
        }
    }


    async function onOngoingEventEdited() {
        if (selectedName === tournament.event && selectedPrize === tournament.prize
            && valueLogoPath.path === selectedLogoPath.path && valueHeaderPath.path === selectedHeaderPath.path
            && valueTrophyPath.path === selectedTrophyPath.path && valueMvpPath.path === selectedMvpPath.path) {
            showNotification("Вы ничего не изменили", "neutral");
        } else if (isEmpty(selectedPrize, "str")) {
            showNotification("Вы не указали приз", "warn");
        } else if (isEmpty(selectedName, "str")) {
            showNotification("Вы не указали название", "warn");
        } else if (selectedName.includes("-")) {
            showNotification('Название не должно содержать "-"', "warn");
        }

        let editedEvent = {
            ...tournament,
            event: selectedName,
            prize: selectedPrize,
            eventFile: selectedLogoPath.path,
            headerFile: selectedHeaderPath.path,
            headerSrc: valueHeaderPath.path !== selectedHeaderPath.path ? URL.createObjectURL(selectedHeaderPath.file) : tournament.headerSrc,
            trophyFile: selectedTrophyPath.path,
            mvpFile: selectedMvpPath.path
        }

        try {
            await request("POST", `editOngoingEventHeader/${tournament.event}`, { event: selectedName, prize: selectedPrize });

            uploadEventImages();

            setTournament(editedEvent);
            showNotification("Турнир успешно изменён", "ok");
            setEditOngoingActive(false);
            navigate(`/event/${selectedName.replaceAll(" ", "-")}/info`)
        } catch (err) {
            showNotification(err.response.data.message, "warn");
        }
    }


    async function uploadEventImages() {
        if (selectedTrophyPath.file !== null) {
            await onImageUploaded(selectedTrophyPath.file, "/events_trophy/", selectedName);
        }

        if (selectedHeaderPath.file !== null) {
            await onImageUploaded(selectedHeaderPath.file, "/events_header/", selectedName);
        }

        if (selectedLogoPath.file !== null) {
            await onImageUploaded(selectedLogoPath.file, "/events_logo/", selectedName);
        }

        if (selectedMvpPath.file !== null) {
            await onImageUploaded(selectedMvpPath.file, "/events_mvp/", selectedName);
        }
    }


    function getName() {
        if (tournament.city === "Выберите город" || tournament.type === "Online")
            return tournament.country;
        return `${tournament.country}, ${tournament.city}`;
    }


    function toggleEndDate() {
        setDateEndSelectorActive(!dateEndSelectorActive);
        toggleOff([0, 2, 3, 4, 5]);
    };


    function toggleDate() {
        setDateSelectorActive(!dateSelectorActive);
        toggleOff([1, 2, 3, 4, 5]);
    };


    function toggleFormat() {
        setFormatSelectorActive(!formatSelectorActive);
        toggleOff([0, 1, 2, 3, 5]);
    }


    function toggleType() {
        setTypeSelectorActive(!typeSelectorActive);
        toggleOff([0, 1, 2, 3, 4]);
    }


    function toggleClass(id) { // функция toggle для селектора
        let temp = [];
        for (let i = 0; i < selectorActive.length; ++i) {
            temp.push(false);
        }
        if (id !== "all") {
            temp[id] = !selectorActive[id];
        }
        setSelectorActive(temp);
    };


    function setPlayer(id, value) { // с её помощью делаем нужную команду заблокированной 
        let temp = [...playersActive];
        let val = indexOf(value, players, "name");

        temp[val] = !temp[val];
        temp[id] = !temp[id];
        setPlayersActive(temp);
    }


    function setPlayerValue(id, value) { // ставим выбранную команду в выбранное поле
        let tempPlayers = [...valuePlayer];
        tempPlayers[id] = value;
        setValuePlayer(tempPlayers);
    }


    function drawButtons() {
        if (!activeTour && ((tournament.registred + 1) <= tournament.total)) {
            return (
                <div className="join_tournament_button display-row-center" style={{ marginTop: "10px" }} onClick={() => setJoinTourWindowActive(true)}>
                    <p>Принять участие в турнире</p>
                </div>
            )
        } else if (activeTour) {
            return (
                <div style={{ display: "flex", flexDirection: "row", margin: "10px auto 0px auto", gap: "10px", justifyContent: "center" }}>
                    <div className="leave_tournament_button display-row-center" style={{ margin: "0" }} onClick={() => setLeaveTourWindowActive(true)}>
                        <p>Отказаться от участия</p>
                    </div>
                    {tournament.format === "2x2" ?
                        <div className="join_tournament_button display-row-center" style={{ margin: "0" }} onClick={() => { setActiveEditTeamWindow(true); toggleClass("all"); }}>
                            <p>Изменить состав команды</p>
                        </div>
                        :
                        <></>
                    }
                </div>
            )
        }
    }


    async function sendRequest(chosenPlayers_) {
        await request("POST", `addNewRequest/${tournament.event}`, { ...playerTeam, chosenPlayers: chosenPlayers_ });
        setTournament({
            ...tournament,
            participants: [...tournament.participants, playerTeam]
        })
        setActiveTour(true);
        showNotification("Вы успешно зарегистрировались на турнир", "ok");
    }


    async function getChosenPlayersFromPlayers() {
        return players.map((player) => {
            return player.name;
        })
    }


    async function onSendRequest() {
        if (tournament.format === "2x2" && players.length > 1) {
            toggleClass("all");
            setValuePlayer([getStoredPlayerNick(), "Выберите игрока"]);
            setChoosedPlayers([getStoredPlayerNick(), "Выберите игрока"])

            let tempPlayersActive = Array(players.length).fill(false);
            let idx = indexOf(getStoredPlayerNick(), players, "name");
            tempPlayersActive[idx] = true;
            setPlayersActive(tempPlayersActive);

            setActiveEditTeamWindow(true);
        } else if (tournament.format === "5x5" && players.length === 5) {
            try {
                await sendRequest(await getChosenPlayersFromPlayers());
            } catch (err) {
                showNotification(err.response.data.message, "warn");
            }
        } else if (tournament.format === "1x1" && !isNotInList(players, "name", getStoredPlayerNick()) && players.length > 0) {
            try {
                await sendRequest([getStoredPlayerNick()]);
            } catch (err) {
                showNotification(err.response.data.message, "warn");
            }
        } else {
            showNotification("Недостаточно участников в команде (проверьте, установлены ли Steam, VK, Faceit)", "warn");
        }
        setJoinTourWindowActive(false);
    }


    async function onLeaveEvent() {
        try {
            await request("POST", `deleteRequest/${tournament.event}/${playerTeam.teamName}`);
            setTournament({
                ...tournament,
                participants: tournament.participants.filter(part => !(part.teamName.includes(playerTeam.teamName))),
                registred: tournament.registred - 1
            })

            setChoosedPlayers([getStoredPlayerNick(), "Выберите игрока"])
            showNotification("Вы отказались от участия в турнире", "ok");
            setLeaveTourWindowActive(false);
            setActiveTour(false);
        } catch (err) {
            showNotification(err.response.data.message, "warn");
        }
    }


    async function onTeamEdited() {
        let result = valuePlayer.some(player => ((player === "Выберите игрока") || (!player.replace(/\s/g, '').length)));

        if (result) {
            showNotification("Вы не указали участников", "warn");
        } else if (isNotInList(players, "name", valuePlayer[1])) {
            showNotification("Выбранного Вами игрока не существует", "warn");
        } else if (valuePlayer[1] === choosedPlayers[1]) {
            showNotification("Вы ничего не изменили");
        } else {
            try {
                if (activeTour) {
                    showNotification("Вы успешно изменили состав", "ok");
                    await request("POST", `editChosenPlayers/${tournament.event}/${playerTeam.teamName}`, valuePlayer);
                } else {
                    await sendRequest(valuePlayer);
                }
                setChoosedPlayers(valuePlayer);
                setActiveEditTeamWindow(false);
            } catch (err) {
                showNotification(err.response.data.message, "warn");
            }
        }
    }


    function isRegistrationActive() {
        let timeUntil = parseDateString(tournament.date.split(" - ")[0], -1).getTime();
        let curDate = new Date();
        let curTime = new Date(curDate.getFullYear(), curDate.getMonth(), curDate.getDate()).getTime();
        return (timeUntil - curTime) > 0;
    }


    return (
        <div>
            {tournament !== null ?
                <>
                    <div className="event_image_header">
                        <div className="crop_header"><img src={tournament.headerSrc} alt={`${tournament.event} header`} /></div>
                    </div>
                    <div className="event_header">
                        <div className="info_wrapper">
                            <div className="row_center_5px">
                                <span>Дата</span>
                                {isAdmin && tournament.status === "upcoming" ? <Editor size="12px" depth={2} onClick={() => { setRegistrationInfoEditorActive(true); setEventInfo() }} /> : <></>}
                            </div>
                            <div className="event_date_wrapper"><p>{getEventDate(tournament.date)}</p></div>
                        </div>

                        <div className="info_wrapper">
                            <span>Приз</span>
                            <div className="event_prize_wrapper"><p>{tournament.prize}</p></div>
                        </div>

                        <div className="info_wrapper">
                            <span>Взнос</span>
                            <div className="event_fee_wrapper"><p>{tournament.fee}</p></div>
                        </div>

                        <div className="info_wrapper">
                            <span>Команды</span>
                            <div className="event_team_wrapper"><p>{tournament.registred}/{tournament.total}</p></div>
                        </div>

                        <div className="info_wrapper">
                            <span>Тип</span>
                            <div className="event_type_wrapper"><p>{tournament.type}</p></div>
                        </div>

                        <div className="info_wrapper">
                            <span>Формат</span>
                            <div className="event_format_wrapper"><p>{tournament.format}</p></div>
                        </div>

                        <div className="info_wrapper">
                            <span>Локация</span>
                            <div className="event_location_wrapper"><FlagName flagPath={tournament.flagPath} country={tournament.country} name={getName()} height='12px' /></div>
                        </div>
                    </div>
                    {
                        tournament.status === "upcoming" && isRegistrationActive() ?
                            <div className="col_center_gap5 event-registration-wrapper">
                                <Timer date={parseDateString(tournament.date.split(" - ")[0], -1)} text={"До конца регистрации: "} style={null} />
                                {(isCap && players !== null) ?
                                    drawButtons() : <></>}
                            </div>
                            :
                            <></>
                    }
                    {
                        eventMapPool !== null ?
                            tournament.status === "upcoming" ? <EventInfo part_header={part_header} status="upcoming" isAdmin={isAdmin} tournament={tournament} setTournament={setTournament} eventMapPool={eventMapPool} /> :
                                tournament.status === "ongoing" ? drawOngoing() :
                                    drawEnded()
                            :
                            <></>
                    }
                </>
                :
                <Preloader />
            }


            {
                isCap && players !== null && playerTeam !== null ?
                    <Login active={activeJoinTourWindow} setActive={setJoinTourWindowActive}>
                        <div className="header_splash_window">
                            <div className="logo_splash_window"></div>
                        </div>
                        <div className="info_text">
                            <p>Принять участие в турнире?</p>
                        </div>
                        <div className="small_buttons_wrapper">
                            <div className="small_dark_button">
                                <input type="submit" value="Нет" onClick={() => { setJoinTourWindowActive(false) }} />
                            </div>
                            <div className="small_grey_button">
                                <input type="submit" value="Да" onClick={() => { onSendRequest() }} />
                            </div>
                        </div>
                    </Login>
                    :
                    <></>
            }


            {
                isCap && tournament !== null && playerTeam !== null ?
                    <Login active={activeLeaveTourWindow} setActive={setLeaveTourWindowActive}>
                        <div className="header_splash_window">
                            <div className="logo_splash_window"></div>
                        </div>
                        <div className="info_text">
                            <p>Вы уверены, что хотите отказаться от участия в турнире?</p>
                        </div>
                        <div className="small_buttons_wrapper">
                            <div className="small_dark_button">
                                <input type="submit" value="Нет" onClick={() => { setLeaveTourWindowActive(false) }} />
                            </div>
                            <div className="small_grey_button">
                                <input type="submit" value="Да" onClick={() => { onLeaveEvent() }} />
                            </div>
                        </div>
                    </Login>
                    :
                    <></>
            }

            {
                isCap && players !== null && playerTeam !== null && valuePlayer !== null && choosedPlayers !== null && selectorActive !== null && playersActive !== null ?
                    <Login active={activeEditTeamWindow} setActive={setActiveEditTeamWindow}>
                        <div className="header_splash_window" onClick={() => toggleClass("all")}>
                            <div className="logo_splash_window"></div>
                        </div>
                        <div className="info_text" onClick={() => toggleClass("all")}>
                            <p>Выберите игроков для участия в турнире</p>
                        </div>
                        <div className="col_center_gap30">
                            <div className="row_center_6">
                                <NonSelectableSelector type={"half"} styleMain={{ zIndex: 2 }} text={getStoredPlayerNick()} styleP={{ color: activeTour ? "var(--white70)" : "white" }} />
                                <SearchableArraySelector issuer={"eventPlayerSelector"} styleMain={{ zIndex: 1 }} startValue={choosedPlayers[1]} value={valuePlayer[1]} selectorActive={selectorActive[1]} toggleClass={toggleClass} index={1} data={players} itemKey={"name"} srcKey={"src"} setDataValue={setPlayerValue} setItem={setPlayer} arrayActive={playersActive} type={"half"} />
                            </div>
                            <div className="full_grey_button" >
                                <input type="submit" value="Подтвердить" onClick={() => { onTeamEdited() }} />
                            </div>
                        </div>
                    </Login>
                    :
                    <></>
            }

            {
                isAdmin && countries !== null && tournament !== null ?
                    <Login active={registrationInfoEditorActive} setActive={setRegistrationInfoEditorActive}>
                        <div className="header_splash_window">
                            <div className="logo_splash_window"></div>
                        </div>
                        <div className="info_text">
                            <p>Укажите информацию о турнире</p>
                        </div>
                        <div className="col_center_gap30">
                            <div className="inside scroll">
                                <DefaultSelector issuer={"datePicker"} type={"datePicker"} styleMain={{ zIndex: 4 }} toggleClass={toggleDate} value={dateSelected} startValue={tournament.date.split(" - ")[0]} setValue={setDateSelected} selectorActive={dateSelectorActive} minDate={new Date()} maxDate={genMaxDate()} setEndDate={setDateEndSelected} />
                                <DefaultSelector issuer={"datePicker"} type={"datePicker"} styleMain={{ zIndex: 3 }} toggleClass={toggleEndDate} value={dateEndSelected} startValue={tournament.date.split(" - ")[1]} setValue={setDateEndSelected} selectorActive={dateEndSelectorActive} minDate={dateSelected !== "Выберите дату начала" ? parseDateString(dateSelected) : new Date()} maxDate={genMaxDate()} />
                                <div className="row_center_6 display-on-start" style={{ zIndex: 2 }}>
                                    <SearchableSelector issuer={"country"} type={"half"} styleMain={null} styleItems={null} setSelectorActive={setCountrySelectorActive} value={countrySelected} startValue={tournament.country} selectorActive={countrySelectorActive} data={countries} setValue={setCountrySelected} setCity={setCitySelected} itemKey={"countryRU"} srcKey={"flagPathMini"} />
                                    {countrySelected !== "Выберите страну" && getItemFromDictByValue(countries, "countryRU", countrySelected) !== undefined ?
                                        <SearchableSelector issuer={"city"} type={"half"} styleMain={null} styleItems={null} setSelectorActive={setCitySelectorActive} value={citySelected} startValue={tournament.city} selectorActive={citySelectorActive} data={getItemFromDictByValue(countries, "countryRU", countrySelected).cities} setValue={setCitySelected} />
                                        :
                                        <></>
                                    }
                                </div>
                                <div className="row_center_6">
                                    <div className="text-field_half">
                                        <input className="text-field_half input" style={{ color: selectedPrize === tournament.prize ? "var(--white70)" : "white" }} type="text" name="prize" value={selectedPrize} placeholder="Укажите приз" onChange={e => { setSelectedPrize(e.target.value) }} />
                                    </div>
                                    <div className="text-field_half">
                                        <input className="text-field_half input" style={{ color: selectedFee === tournament.fee ? "var(--white70)" : "white" }} type="text" name="prize" value={selectedFee} placeholder="Укажите взнос" onChange={e => { setSelectedFee(e.target.value) }} />
                                    </div>
                                </div>
                                <div className="row_center_6" style={{ zIndex: 1 }}>
                                    <div className="text-field_half">
                                        <DefaultSelector issuer={"default"} type={"half"} styleMain={null} styleItems={null} toggleClass={toggleFormat} value={selectedFormat} startValue={tournament.format} setValue={setSelectedFormat} selectorActive={formatSelectorActive} data={["1x1", "2x2", "5x5"]} />
                                    </div>
                                    <div className="text-field_half">
                                        <DefaultSelector issuer={"default"} type={"half"} styleMain={null} styleItems={null} toggleClass={toggleType} value={selectedType} startValue={tournament.type} setValue={setSelectedType} selectorActive={typeSelectorActive} data={["Lan", "Online"]} />
                                    </div>
                                </div>
                                <div className="row_center_6">
                                    <FileLoader value={selectedLogoPath.path} startValue={valueLogoPath.path} handleImageUploaded={handleImageUploaded} setSelectedFile={setSelectedLogoPath} />
                                    <FileLoader value={selectedHeaderPath.path} startValue={valueHeaderPath.path} handleImageUploaded={handleImageUploaded} setSelectedFile={setSelectedHeaderPath} />
                                </div>
                                <div className="row_center_6">
                                    <FileLoader value={selectedTrophyPath.path} startValue={valueTrophyPath.path} handleImageUploaded={handleImageUploaded} setSelectedFile={setSelectedTrophyPath} />
                                    <FileLoader value={selectedMvpPath.path} startValue={valueMvpPath.path} handleImageUploaded={handleImageUploaded} setSelectedFile={setSelectedMvpPath} />
                                </div>
                                <div className="text-field">
                                    <input className="text-field_half input" style={{ color: selectedName === tournament.event ? "var(--white70)" : "white" }} type="text" name="eventName" value={selectedName} placeholder="Укажите название" onChange={e => { setSelectedName(e.target.value) }} />
                                </div>
                            </div>
                            <div className="col_center_gap_20">
                                <div className="full_grey_button">
                                    <input type="submit" value="Подтвердить" onClick={() => onRegistrationEvent()} />
                                </div>
                                <div className="leave_tournament_button display-row-center" style={{ margin: 0, width: "198px", height: "38px", padding: 0 }} onClick={() => { setRegistrationInfoEditorActive(false); setDeleteEventActive(true) }}>
                                    <p style={{ margin: 0, fontSize: "14px" }}>Удалить турнир</p>
                                </div>
                            </div>
                        </div>
                    </Login>
                    :
                    <></>
            }

            {
                isAdmin && tournament !== null ?
                    <Login active={deleteEventActive} setActive={setDeleteEventActive}>
                        <div className="header_splash_window">
                            <div className="logo_splash_window"></div>
                        </div>
                        <div className="info_text">
                            <p>Вы уверены, что хотите удалить турнир {tournament.event}?</p>
                        </div>
                        <div className="small_buttons_wrapper">
                            <div className="small_dark_button">
                                <input type="submit" value="Нет" onClick={() => deleteEventActive ? setDeleteEventActive(!deleteEventActive) : null} />
                            </div>
                            <div className="small_grey_button">
                                <input type="submit" value="Да" onClick={() => deleteEventActive ? setDeleteEventActive(!deleteEventActive) : null} />
                            </div>
                        </div>
                    </Login>
                    :
                    <></>
            }

            {
                isAdmin && tournament !== null ?
                    <Login active={editOngoingActive} setActive={setEditOngoingActive}>
                        <div className="header_splash_window">
                            <div className="logo_splash_window"></div>
                        </div>
                        <div className="info_text">
                            <p>Укажите информацию о турнире</p>
                        </div>
                        <div className="col_center_gap30">
                            <div className="inside">
                                <div className="row_center_6">
                                    <div className="text-field_half">
                                        <input className="text-field_half input" style={{ color: selectedName === tournament.event ? "var(--white70)" : "white" }} type="text" name="prize" value={selectedName} placeholder="Укажите название" onChange={e => { setSelectedName(e.target.value) }} />
                                    </div>
                                    <div className="text-field_half">
                                        <input className="text-field_half input" style={{ color: selectedPrize === tournament.prize ? "var(--white70)" : "white" }} type="text" name="prize" value={selectedPrize} placeholder="Укажите приз" onChange={e => { setSelectedPrize(e.target.value) }} />
                                    </div>
                                </div>
                                <div className="row_center_6">
                                    <FileLoader value={selectedLogoPath.path} startValue={valueLogoPath.path} handleImageUploaded={handleImageUploaded} setSelectedFile={setSelectedLogoPath} />
                                    <FileLoader value={selectedHeaderPath.path} startValue={valueHeaderPath.path} handleImageUploaded={handleImageUploaded} setSelectedFile={setSelectedHeaderPath} />
                                </div>
                                <div className="row_center_6 ">
                                    <FileLoader value={selectedTrophyPath.path} startValue={valueTrophyPath.path} handleImageUploaded={handleImageUploaded} setSelectedFile={setSelectedTrophyPath} />
                                    <FileLoader value={selectedMvpPath.path} startValue={valueMvpPath.path} handleImageUploaded={handleImageUploaded} setSelectedFile={setSelectedMvpPath} />
                                </div>
                            </div>
                            <div className="col_center_gap_20">
                                <div className="full_grey_button">
                                    <input type="submit" value="Подтвердить" onClick={() => { onOngoingEventEdited() }} />
                                </div>
                                <div className="leave_tournament_button display-row-center" style={{ margin: 0, width: "198px", height: "38px", padding: 0 }} onClick={() => { setEditOngoingActive(false); setDeleteEventActive(true) }}>
                                    <p style={{ margin: 0, fontSize: "14px" }}>Удалить турнир</p>
                                </div>
                            </div>
                        </div>
                    </Login>
                    :
                    <></>
            }
        </div >
    );
}

export default Event;