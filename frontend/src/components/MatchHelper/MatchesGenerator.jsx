import React, { useState, useRef } from "react";
import "../../pages/Matches/Matches.css";
import Editor from "../Editor/Editor";
import Login from "../Login/Login";
import OngoingMatchMaker from "../MatchMacker/OngoingMatchMaker";
import UpcomingMatchMaker from "../MatchMacker/UpcomingMatchMaker";
import SearchableSelector from "../Selector/SearchableSelector";
import SearchableArraySelector from "../Selector/SearchableArraySelector";
import DefaultSelector from "../Selector/DefaultSelector";
import SetMatchInfo from "./SetMatchInfo";
import { isNotInList, indexOf, genMaxDate, parseDateString, fixNumbers, isCorrectTime, findKeyByName, getTimeOfString, resetRef, showNotification } from "../../Utils/Utils";
import { request } from "../../Utils/MyAxios";

// TODO: ЕСЛИ У ТУРНИРА НЕ ХВАТАЕТ КАРТ НА КАКОЙ-ТО ФОРМАТ(бо3, бо2, бо5 и тп) - не давать создавать матч такого формата
function MatchesGenerator(props) {
    const [ongoingEditorActive, setOngoingEditorActive] = useState(false); //состояния модального окна для редактирования текущих матчей
    const [comingEditorActive, setComingEditorActive] = useState(false); //состояния модального окна для редактирования будущих матчей
    const [createMatchActive, setCreateMatchActive] = useState(false);

    const [chooseComingMatchActive, setChooseComingMatchActive] = useState(false); //выбираем будущий матч

    const [selectedFormat, setSelectedFormat] = useState('Выберите формат'); // формат (bo1 и тп)
    const [formatSelectorActive, setFormatSelectorActive] = useState(false);

    const [selectedTier, setSelectedTier] = useState('Выберите важность'); // важность
    const [tierSelectorActive, setTierSelectorActive] = useState(false);

    const [teamsActive, setTeamsActive] = useState(null); // состояния команд - выбрана ли команда(чтоб блокировать ее)
    const [valueTeam, setValueTeam] = useState('Выберите матч'); // Выбрать матч надо

    const [matchSelectorActive, setMatchSelectorActive] = useState(false);

    const [valueDate, setValueDate] = useState(''); // Это для даты выбранного матча
    const [dateSelected, setDateSelected] = useState(''); // здесь хранится выбраная дата
    const [dateSelectorActive, setDateSelectorActive] = useState(false); // открыт/закрыт календарь

    const [valueTime, setValueTime] = useState(''); // Это для времени выбранного матча
    const [timeSelected, setTimeSelected] = useState(''); // здесь хранится выбраное время
    const [timeSelectorActive, setTimeSelectorActive] = useState(false); // открыт/закрыт выбор времени

    const [valueEvent, setValueEvent] = useState(''); // Это для турнира выбранного матча
    const [eventSelected, setEventSelected] = useState(''); // здесь хранится выбраный турнир
    const [eventSelectorActive, setEventSelectorActive] = useState(false); // открыт/закрыт выбор турнира

    const [selectedLeftTeam, setSelectedLeftTeam] = useState(''); // левая команда
    const [selectedRightTeam, setSelectedRightTeam] = useState(''); // правая команда    

    const [valueLeftTeam, setValueLeftTeam] = useState(''); // левая команда
    const [valueRightTeam, setValueRightTeam] = useState(''); // правая команда

    const [leftTeamSelectorActive, setLeftTeamSelectorActive] = useState(false); // открыт/закрыт выбор левой команды
    const [rightTeamSelectorActive, setRightTeamSelectorActive] = useState(false); // открыт/закрыт выбор правой команды

    const [matchId, setMatchId] = useState(null);

    const [matchEditorActive, setMatchEditorActive] = useState(false); //состояния модального окна для редактирования текущих матчей
    const [deleteMatchActive, setDeleteMatchActive] = useState(false);

    const ref = useRef(null);


    function getMatchId() {
        let id = -1;
        props.upcoming_matches.flatMap(day => day.matches).map((match) => {
            if (match.matchId > id) {
                id = match.matchId;
            }
        });

        props.ongoing_matches.map((match) => {
            if (match.matchId > id) {
                id = match.matchId;
            }
        });

        return id + 1;
    }


    async function onMatchDelete() {
        props.upcoming_matches.forEach(function (day, index, object) {
            day.matches = day.matches.filter(match => match.matchId !== matchId);

            if (day.matches.length === 0) {
                object.splice(index, 1);
            }
        });


        props.ongoing_matches.forEach(function (match, index, object) {
            if (match.matchId === matchId) {
                object.splice(index, 1);
            }
        });

        try {
            await request("POST", `deleteMatch/${valueEvent}/${matchId}`, {});
            showNotification("Матч успешно удалён", "ok");
        } catch (err) {
            showNotification(err.response.data.message, "warn");
        }
    }


    async function onMatchCreate() {
        if (dateSelected === "Выберите дату") {
            showNotification("Вы не указали дату", "warn");
        } else if (timeSelected === "Выберите время") {
            showNotification("Вы не указали время", "warn");
        } else if (eventSelected === "Выберите турнир") {
            showNotification("Вы не указали турнир", "warn");
        } else if (selectedLeftTeam === "Выберите левую команду") {
            showNotification("Вы не указали левую команду", "warn");
        } else if (selectedRightTeam === "Выберите правую команду") {
            showNotification("Вы не указали правую команду", "warn");
        } else if (selectedFormat === "Выберите формат") {
            showNotification("Вы не указали формат", "warn");
        } else if (selectedTier === "Выберите важность") {
            showNotification("Вы не указали важность", "warn");
        } else if (!isCorrectTime(getMinTime(), timeSelected)) {
            showNotification("Вы указали неверное время матча", "warn");
        } else if (isNotInList(props.part, "name", selectedRightTeam)) {
            showNotification("Вы выбрали правую команду, которой не существует", "warn");
        } else if (isNotInList(props.part, "name", selectedLeftTeam)) {
            showNotification("Вы выбрали левую команду, которой не существует", "warn");
        } else if (selectedLeftTeam === selectedRightTeam) {
            showNotification("Вы выбрали одинаковые команды", "warn");
        } else {
            let newMatch = {
                date: dateSelected,
                event: eventSelected,
                leftTeam: selectedLeftTeam,
                leftTag: findKeyByName(props.part, selectedLeftTeam, "tag", "name"),
                rightTeam: selectedRightTeam,
                rightTag: findKeyByName(props.part, selectedRightTeam, "tag", "name"),
                maps: Array(parseInt(selectedFormat[2])).fill({ mapName: "TBA", status: "upcoming" }),
                tier: selectedTier[0],
                time: timeSelected,
                matchId: getMatchId()
            }

            try {
                await request("POST", "/createMatch", { ...newMatch, description: ref.current.value });

                newMatch["images"] = {
                    eventSrc: findKeyByName(props.event, eventSelected, "eventSrc", "name"),
                    leftTeamSrc: findKeyByName(props.part, selectedLeftTeam, "logo", "name"),
                    rightTeamSrc: findKeyByName(props.part, selectedRightTeam, "logo", "name")
                };

                if (props.upcoming_matches.filter(day => day.date === dateSelected).length > 0) {
                    props.upcoming_matches.map((day) => {
                        if (day.date === dateSelected) {
                            day.matches.push(newMatch);
                            day.matches.sort((a, b) => getTimeOfString(a.time) - getTimeOfString(b.time));
                        }
                    })
                } else {
                    props.upcoming_matches.push({ date: dateSelected, matches: [newMatch] });
                    props.upcoming_matches.sort((a, b) => parseDateString(a.date).getTime() - parseDateString(b.date).getTime());
                }

                showNotification("Матч успешно создан", "ok");
            } catch (err) {
                showNotification(err.response.data.message, "warn");
            }

            setCreateMatchActive(false);
        }
    }


    function setLeftTeam(team) {
        const idx = indexOf(team, props.part, "tag");
        teamsActive[idx] = !teamsActive[idx];

        setValueLeftTeam(team);
        setSelectedLeftTeam(team);
    }


    function setRightTeam(team) {
        const idx = indexOf(team, props.part, "tag");
        teamsActive[idx] = !teamsActive[idx];

        setValueRightTeam(team);
        setSelectedRightTeam(team);
    }


    function isMatchExits() {
        if (valueTeam.includes(" vs. ")) {
            let splited = valueTeam.split(" vs. ");
            return !isNotInList(props.part, "tag", splited[0]) && !isNotInList(props.part, "tag", splited[1]);
        }
        return false;
    }


    function toggleDate() {
        setDateSelectorActive(!dateSelectorActive)
    };


    function toggleTime() {
        setTimeSelectorActive(!timeSelectorActive);
    }


    function toggleFormat() {
        setFormatSelectorActive(!formatSelectorActive);
    }


    function toggleTier() {
        setTierSelectorActive(!tierSelectorActive);
    }


    function toggleLeftTeam() {
        setLeftTeamSelectorActive(!leftTeamSelectorActive);
    }


    function toggleRightTeam() {
        setRightTeamSelectorActive(!rightTeamSelectorActive);
    }


    function setTeam(id, value) { // с её помощью делаем нужную команду заблокированной 
        let temp = [...teamsActive];
        let val = indexOf(value, props.part, "name");

        // TODO: СДЕЛАТЬ У ВСЕХ ТАКИХ СЕТТЕРОВ ПРОВЕРКИ ПОХОЖИЕ. ДЛЯ МАССИВОВ ТАМ СДЕЛАТЬ ОБЩИЙ ПРОГОН
        // МОЖНО ПРОВЕРЯТЬ МАССИВЫ СО ЗНАЧЕНИЯМИ. ЕСЛИ ЕСТЬ ПОВТОРЕНИЕ, ТО НЕ ИЗМЕНЯТЬ СОСТОЯНИЕ БЛОКИРОВКИ
        // ВООБЩЕ ПОТОМ ВЫНЕСТИ ЭТО В ОТДЕЛЬНЫЙ КЛАСС
        // СУТЬ ПРОВЕРКИ В ТОМ, ЧТОБ В СЕЛЕКТОРАХ НЕЛЬЗЯ БЫЛО ВЫБРАТЬ ОДНУ КОМАНДУ, А ЗАТЕМ, НАПИСАВ В ДРУГОМ СЕЛЕКТОРЕ, РАЗБЛОКИРОВАТЬ ЕЕ

        // if (Math.abs(selectedLeftTeam.length - selectedRightTeam.length) !== 1 && (!selectedLeftTeam.startsWith(selectedRightTeam) || !selectedRightTeam.startsWith(selectedLeftTeam)) || selectedRightTeam === "Выберите правую команду" || selectedLeftTeam === "Выберите левую команду") {
        //   temp[val] = !temp[val];
        //   temp[id] = !temp[id];
        //   setTeamsActive(temp);
        // }

        temp[val] = !temp[val];
        temp[id] = !temp[id];
        setTeamsActive(temp);
    }


    function getEventsByDate() {
        let allMatches = props.upcoming_matches.flatMap(day => day.matches);
        let filtredUpcomingMatches = allMatches.filter(match => parseDateString(match.date).getTime() === parseDateString(dateSelected).getTime())

        let unique = filtredUpcomingMatches.filter((match, i) =>
            i === filtredUpcomingMatches.findIndex(match2 => match.event === match2.event)
        );

        return unique.map(match => ({ event: match.event, eventSrc: match.images.eventSrc }));
    }


    function getMatchesByEvent(ev) {
        return props.upcoming_matches.flatMap(day => day.matches).filter(match => (parseDateString(match.date).getTime() === parseDateString(dateSelected).getTime()) && (match.event === ev));
    }


    function getMinDate() {
        let date = new Date();

        if (date.getHours() === 23 && date.getMinutes() >= 49) {
            date.setDate(date.getDate() + 1);
        }
        return date;
    }


    function getMinTime() {
        let parsedDate = parseDateString(dateSelected);
        let date = dateSelected !== "Выберите дату" ? new Date(parsedDate.getFullYear(), parsedDate.getMonth(), parsedDate.getDate() - 1) : new Date();
        let dateHours;
        let dateMinutes;

        if (dateSelected !== "Выберите дату" && new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate() + 1).getTime() === parseDateString(dateSelected).getTime()) {
            dateMinutes = new Date().getMinutes();
            dateHours = new Date().getHours();
            dateMinutes += 11;
        } else {
            dateMinutes = date.getMinutes();
            dateHours = date.getHours();
        }

        if (dateMinutes >= 60) {
            dateHours += 1;
            if (dateHours >= 24) {
                dateHours -= 24;
            }
            dateMinutes -= 60;
        }
        return `${fixNumbers(dateHours)}:${fixNumbers(dateMinutes)}`;
    }


    return (
        <div>
            {props.ongoing_matches !== null && props.ongoing_matches.length > 0 ?
                <div className="matches_header">

                    <div className="row_center_5px">
                        <p>Текущие матчи</p>
                        {props.isAdmin && props.part !== null ? <Editor size="12px" depth={2} onClick={() => { setOngoingEditorActive(true); setMatchSelectorActive(false); setTeamsActive(Array(props.part.length).fill(false)); setValueTeam("Выберите матч") }} /> : <></>}
                    </div>
                </div>
                :
                <></>
            }
            <div className="matches">
                <div className="col_center_gap10">
                    {
                        props.ongoing_matches !== null ?
                            props.ongoing_matches.map((match) =>
                                <OngoingMatchMaker {...match} key={match.matchId} />
                            )
                            :
                            <></>
                    }
                </div>
            </div>

            {props.upcoming_matches !== null && props.upcoming_matches.length > 0 || props.isAdmin ?
                <div className="matches_header">
                    <div className="row_center_5px">
                        <p>Ближайшие матчи</p>
                        {props.isAdmin && props.part !== null ? <Editor size="12px" depth={2} onClick={() => setComingEditorActive(true)} /> : <></>}
                    </div>
                </div>
                :
                <></>
            }
            <div className="matches">
                {
                    props.upcoming_matches !== null ?
                        props.upcoming_matches.map((day) =>
                            <UpcomingMatchMaker {...day} key={day.date} />
                        )
                        :
                        <></>
                }
            </div>

            {props.isAdmin ?
                <Login active={ongoingEditorActive} setActive={setOngoingEditorActive} key={"ongoingEditorActive"}>
                    <div className="header_splash_window">
                        <div className="logo_splash_window"></div>
                    </div>
                    <div className="info_text">
                        <p>Укажите информацию о матче</p>
                    </div>
                    <div className="inside">
                        <div className="col_center_gap30">
                            <SearchableSelector issuer={"matches"} type={"match"} styleMain={{ zIndex: 1 }} styleItems={null} setSelectorActive={setMatchSelectorActive} value={valueTeam} startValue={"Выберите матч"} selectorActive={matchSelectorActive} data={props.ongoing_matches} setValue={setValueTeam} setValueDate={setValueDate} setDateSelected={setDateSelected} dateKey={"date"} setTimeSelected={setTimeSelected} setValueTime={setValueTime} timeKey={"time"} setEventSelected={setEventSelected} setValueEvent={setValueEvent} eventKey={"event"} setItem={setLeftTeam} setSecondItem={setRightTeam} itemKey={"leftTag"} srcKey={"leftTeamSrc"} itemSecondKey={"rightTag"} srcSecondKey={"rightTeamSrc"} setMatchId={setMatchId} />
                            {isMatchExits() ?
                                <div className="full_grey_button">
                                    <input type="submit" value="Подтвердить" onClick={() => { setMatchEditorActive(true); setOngoingEditorActive(false) }} />
                                </div>
                                :
                                <></>
                            }
                        </div>
                    </div>
                </Login>
                :
                <></>
            }


            {props.isAdmin ?
                <Login active={matchEditorActive} setActive={setMatchEditorActive} key={"matchEditorActive"}>
                    <SetMatchInfo
                        matchId={matchId}
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
                        valueEvent={valueEvent}
                        eventSelectorActive={eventSelectorActive}
                        setEventSelectorActive={setEventSelectorActive}
                        setEventSelected={setEventSelected}
                        selectedLeftTeam={selectedLeftTeam}
                        valueLeftTeam={valueLeftTeam}
                        leftTeamSelectorActive={leftTeamSelectorActive}
                        toggleLeftTeam={toggleLeftTeam}
                        setSelectedLeftTeam={setSelectedLeftTeam}
                        setTeam={setTeam}
                        selectedRightTeam={selectedRightTeam}
                        valueRightTeam={valueRightTeam}
                        rightTeamSelectorActive={rightTeamSelectorActive}
                        toggleRightTeam={toggleRightTeam}
                        setSelectedRightTeam={setSelectedRightTeam}
                        setMatchEditorActive={setMatchEditorActive}
                        setDeleteMatchActive={setDeleteMatchActive}
                        teamsActive={teamsActive}
                        teams={props.part}
                        allEvents={props.event}
                        ongoingMatches={props.ongoing_matches}
                        upcomingMatches={props.upcoming_matches}
                    />
                </Login>
                :
                <></>
            }

            {props.isAdmin ?
                <Login active={deleteMatchActive} setActive={setDeleteMatchActive} key={"deleteMatchActive"}>
                    <div className="header_splash_window">
                        <div className="logo_splash_window"></div>
                    </div>
                    <div className="info_text">
                        <p>Вы уверены, что хотите удалить матч {valueTeam}?</p>
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

            {props.isAdmin ?
                <Login active={comingEditorActive} setActive={setComingEditorActive} key={"comingEditorActive"}>
                    <div className="header_splash_window">
                        <div className="logo_splash_window"></div>
                    </div>
                    <div className="info_text">
                        <p>Что Вы хотите сделать?</p>
                    </div>
                    {props.part !== null ?
                        <div className="buttons_wrapper">
                            <div className="dark_button">
                                <input type="submit" value="Изменить матч" onClick={() => { setComingEditorActive(false); setChooseComingMatchActive(true); setEventSelectorActive(false); setDateSelected("Выберите дату"); setEventSelected("Выберите турнир"); setTeamsActive(Array(props.part.length).fill(false)) }} />
                            </div>
                            <div className="full_grey_button" style={{ margin: 0 }}>
                                <input type="submit" value="Создать матч" onClick={() => { setComingEditorActive(false); setCreateMatchActive(true); setDateSelected("Выберите дату"); setEventSelected("Выберите турнир"); setTimeSelected("Выберите время"); setSelectedLeftTeam("Выберите левую команду"); setSelectedRightTeam("Выберите правую команду"); setSelectedFormat("Выберите формат"); setSelectedTier("Выберите важность"); resetRef(ref); setTeamsActive(Array(props.part.length).fill(false)) }} />
                            </div>
                        </div>
                        :
                        <></>
                    }
                </Login>
                :
                <></>
            }

            {props.isAdmin ?
                <Login active={chooseComingMatchActive} setActive={setChooseComingMatchActive} key={"chooseComingMatchActive"}>
                    <div className="header_splash_window">
                        <div className="logo_splash_window"></div>
                    </div>
                    <div className="info_text">
                        <p>Укажите информацию о матче</p>
                    </div>
                    <div className="col_center_gap10">
                        <DefaultSelector issuer={"datePicker"} type={"datePicker"} styleMain={{ zIndex: 3 }} toggleClass={toggleDate} value={dateSelected} startValue={"Выберите дату"} setValue={setDateSelected} selectorActive={dateSelectorActive} minDate={new Date()} maxDate={genMaxDate()} setEventSelected={setEventSelected} setValueTeam={setValueTeam} />
                        {dateSelected !== "Выберите дату" && props.upcoming_matches !== null ?
                            <div className="col_center_gap10">
                                <SearchableSelector issuer={"eventSelector"} type={"half"} styleMain={{ zIndex: 2 }} styleItems={null} setSelectorActive={setEventSelectorActive} value={eventSelected} startValue={"Выберите турнир"} selectorActive={eventSelectorActive} data={getEventsByDate()} setValue={setEventSelected} itemKey={"event"} srcKey={"eventSrc"} setValueTeam={setValueTeam} />

                                {eventSelected !== "Выберите турнир" && !isNotInList(props.event, "name", eventSelected) && props.upcoming_matches !== null ?
                                    <div className="col_center_gap30">
                                        <SearchableSelector issuer={"matches"} type={"match"} styleMain={{ zIndex: 1 }} styleItems={null} setSelectorActive={setMatchSelectorActive} value={valueTeam} startValue={"Выберите матч"} selectorActive={matchSelectorActive} data={getMatchesByEvent(eventSelected)} setValue={setValueTeam} setValueDate={setValueDate} setDateSelected={setDateSelected} dateKey={"date"} setTimeSelected={setTimeSelected} setValueTime={setValueTime} timeKey={"time"} setEventSelected={setEventSelected} setValueEvent={setValueEvent} eventKey={"event"} setItem={setLeftTeam} setSecondItem={setRightTeam} itemKey={"leftTag"} srcKey={"leftTeamSrc"} itemSecondKey={"rightTag"} srcSecondKey={"rightTeamSrc"} setMatchId={setMatchId} />
                                        {isMatchExits() ?
                                            <div className="full_grey_button">
                                                <input type="submit" value="Подтвердить" onClick={() => { setMatchEditorActive(true); setChooseComingMatchActive(false) }} />
                                            </div>
                                            :
                                            <></>
                                        }
                                    </div>
                                    :
                                    <></>
                                }
                            </div>
                            :
                            <></>
                        }
                    </div>
                </Login>
                :
                <></>
            }

            {props.isAdmin ?
                <Login active={createMatchActive} setActive={setCreateMatchActive} key={"createMatchActive"}>
                    <div className="header_splash_window">
                        <div className="logo_splash_window"></div>
                    </div>
                    <div className="info_text">
                        <p>Укажите информацию о матче</p>
                    </div>
                    <div className="col_center_gap30">
                        <div className="inside scroll">
                            <DefaultSelector issuer={"datePicker"} type={"datePicker"} styleMain={{ zIndex: 3 }} toggleClass={toggleDate} value={dateSelected} startValue={"Выберите дату"} setValue={setDateSelected} selectorActive={dateSelectorActive} minDate={getMinDate()} maxDate={genMaxDate()} />
                            <div className="row_center_6">
                                <div className="text-field_half">
                                    <DefaultSelector issuer={"timePicker"} type={"half"} styleMain={{ zIndex: 2 }} styleItems={null} toggleClass={toggleTime} value={timeSelected} setValue={setTimeSelected} startValue={getMinTime()} selectorActive={timeSelectorActive} />
                                </div>
                                {props.event !== null ?
                                    <SearchableSelector issuer={"eventSelector"} type={"half"} styleMain={{ zIndex: 2 }} setSelectorActive={setEventSelectorActive} value={eventSelected} startValue={"Выберите турнир"} selectorActive={eventSelectorActive} data={props.event} setValue={setEventSelected} itemKey={"name"} srcKey={"eventSrc"} />
                                    :
                                    <></>
                                }
                            </div>
                            {props.part !== null && teamsActive !== null ?
                                <div className="row_center_6">
                                    <SearchableArraySelector issuer={"eventTeamSelector"} styleMain={{ zIndex: 1 }} toggleClass={toggleLeftTeam} value={selectedLeftTeam} startValue={"Выберите левую команду"} selectorActive={leftTeamSelectorActive} data={props.part} arrayActive={teamsActive} itemKey={"name"} setDataValue={setSelectedLeftTeam} setItem={setTeam} srcKey={"logo"} type={"half"} />
                                    <SearchableArraySelector issuer={"eventTeamSelector"} styleMain={{ zIndex: 1 }} toggleClass={toggleRightTeam} value={selectedRightTeam} startValue={"Выберите правую команду"} selectorActive={rightTeamSelectorActive} data={props.part} arrayActive={teamsActive} itemKey={"name"} setDataValue={setSelectedRightTeam} setItem={setTeam} srcKey={"logo"} type={"half"} />
                                </div>
                                :
                                <></>
                            }
                            <div className="description_field">
                                <textarea type="text" ref={ref} placeholder="Введите описание" style={{ width: "434px", color: "white", fontSize: "16px" }}></textarea>
                            </div>
                            <div className="row_center_6">
                                <div className="text-field_half" style={{ zIndex: 0 }}>
                                    <DefaultSelector issuer={"default"} type={"half"} styleMain={null} styleItems={null} toggleClass={toggleFormat} value={selectedFormat} startValue={"Выберите формат"} setValue={setSelectedFormat} selectorActive={formatSelectorActive} data={["bo1", "bo2", "bo3", "bo5", "bo7"]} />
                                </div>
                                <div className="text-field_half" style={{ zIndex: 0 }}>
                                    <DefaultSelector issuer={"tier"} type={"half"} styleMain={null} styleItems={null} toggleClass={toggleTier} value={selectedTier} startValue={"Выберите важность"} setValue={setSelectedTier} selectorActive={tierSelectorActive} depth={"../../"} />
                                </div>
                            </div>
                        </div>
                        <div className="join_tournament_button display-row-center" style={{ margin: 0, width: "198px", height: "38px", padding: 0 }} onClick={() => { onMatchCreate() }}>
                            <p style={{ margin: 0, fontSize: "14px" }}>Создать матч</p>
                        </div>
                    </div>
                </Login>
                :
                <></>
            }
        </div>
    );
}

export default MatchesGenerator;