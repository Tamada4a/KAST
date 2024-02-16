import React from "react";
import DefaultSelector from "../Selector/DefaultSelector";
import SearchableSelector from "../Selector/SearchableSelector";
import SearchableArraySelector from "../Selector/SearchableArraySelector";
import "../../pages/Matches/Matches.css";
import { request } from "../../Utils/MyAxios";
import { genMaxDate, parseDateString, isNotInList, findKeyByName, isCorrectTime, getTimeOfString, getItemFromDictByValue, showNotification } from "../../Utils/Utils";

function SetMatchInfo(props) {
    const matchId = props.matchId;
    const toggleDate = props.toggleDate;
    const dateSelected = props.dateSelected;
    const valueDate = props.valueDate;
    const dateSelectorActive = props.dateSelectorActive;
    const setDateSelected = props.setDateSelected;
    const timeSelected = props.timeSelected;
    const valueTime = props.valueTime;
    const timeSelectorActive = props.timeSelectorActive;
    const setTimeSelected = props.setTimeSelected;
    const toggleTime = props.toggleTime;
    const eventSelected = props.eventSelected;
    const valueEvent = props.valueEvent;
    const eventSelectorActive = props.eventSelectorActive;
    const setEventSelected = props.setEventSelected;
    const setEventSelectorActive = props.setEventSelectorActive;
    const selectedLeftTeam = props.selectedLeftTeam;
    const valueLeftTeam = props.valueLeftTeam;
    const leftTeamSelectorActive = props.leftTeamSelectorActive;
    const toggleLeftTeam = props.toggleLeftTeam;
    const setSelectedLeftTeam = props.setSelectedLeftTeam;
    const setTeam = props.setTeam;
    const selectedRightTeam = props.selectedRightTeam;
    const valueRightTeam = props.valueRightTeam;
    const rightTeamSelectorActive = props.rightTeamSelectorActive;
    const toggleRightTeam = props.toggleRightTeam;
    const setSelectedRightTeam = props.setSelectedRightTeam;
    const setMatchEditorActive = props.setMatchEditorActive;
    const setDeleteMatchActive = props.setDeleteMatchActive;
    const teamsActive = props.teamsActive;
    const teams = props.teams;
    const allEvents = props.allEvents;
    const ongoingMatches = props.ongoingMatches;
    const upcomingMatches = props.upcomingMatches;


    function getMatchImages() {
        const [leftTeamSrc, rightTeamSrc, eventSrc] = [
            findKeyByName(teams, selectedLeftTeam, "logo", "name"),
            findKeyByName(teams, selectedRightTeam, "logo", "name"),
            findKeyByName(allEvents, eventSelected, "eventSrc", "name")
        ];
        return { leftTeamSrc, rightTeamSrc, eventSrc };
    }


    function sortMatches(editedMatch) {
        if (upcomingMatches.filter(day => day.date === dateSelected).length > 0) {
            upcomingMatches.map((day) => {
                if (day.date === dateSelected) {
                    day.matches.push(editedMatch);
                    day.matches.sort((a, b) => getTimeOfString(a.time) - getTimeOfString(b.time));
                }
            })
        } else {
            upcomingMatches.push({ date: dateSelected, matches: [editedMatch] });
            upcomingMatches.sort((a, b) => parseDateString(a.date).getTime() - parseDateString(b.date).getTime());
        }
    }


    function stringDateToFullDate() {
        let splitTime = timeSelected.split(":");
        let splitDate = dateSelected.split(".");

        return new Date(parseInt(splitDate[2]), parseInt(splitDate[1]) - 1, parseInt(splitDate[0]), parseInt(splitTime[0]), parseInt(splitTime[1]), 0);
    }


    async function onAccept() {
        if (dateSelected === valueDate && timeSelected === valueTime && eventSelected === valueEvent && selectedLeftTeam === valueLeftTeam && selectedRightTeam === valueRightTeam) {
            showNotification("Вы ничего не изменили", "neutral");
        } else if (selectedRightTeam === selectedLeftTeam || selectedRightTeam === valueLeftTeam || selectedLeftTeam === valueRightTeam) {
            showNotification("Вы выбрали две одинаковые команды", "warn");
        } else if (isNotInList(teams, "name", selectedRightTeam)) {
            showNotification("Вы выбрали правую команду, которой не существует", "warn");
        } else if (isNotInList(teams, "name", selectedLeftTeam)) {
            showNotification("Вы выбрали левую команду, которой не существует", "warn");
        } else if (isNotInList(allEvents, "name", eventSelected)) {
            showNotification("Вы выбрали турнир, который не существует", "warn");
        } else if (!isCorrectTime(valueTime, timeSelected)) {
            showNotification("Вы указали неверное время матча", "warn");
        } else {

            if (upcomingMatches !== undefined && ongoingMatches !== undefined) {
                upcomingMatches.forEach(function (day, index, object) {
                    day.matches.map(match => {
                        if (match.matchId === matchId) {
                            let editedMatch = {
                                ...match,
                                date: dateSelected,
                                event: eventSelected,
                                leftTeam: selectedLeftTeam,
                                leftTag: findKeyByName(teams, selectedLeftTeam, "tag", "name"),
                                rightTeam: selectedRightTeam,
                                rightTag: findKeyByName(teams, selectedRightTeam, "tag", "name"),
                                time: timeSelected,
                                images: getMatchImages()
                            }
                            sortMatches(editedMatch);
                        }
                    })

                    day.matches = day.matches.filter(match => match.matchId !== matchId);

                    if (day.matches.length === 0) {
                        object.splice(index, 1);
                    }
                });

                ongoingMatches.forEach(function iter(match, index, object) {
                    if (match.matchId === matchId) {
                        match.date = dateSelected;
                        match.time = timeSelected;
                        match.event = eventSelected;
                        match.leftTag = findKeyByName(teams, selectedLeftTeam, "tag", "name");
                        match.leftTeam = selectedLeftTeam;
                        match.rightTag = findKeyByName(teams, selectedRightTeam, "tag", "name");
                        match.rightTeam = selectedRightTeam;
                        match.images = getMatchImages();

                        if (getTimeOfString(timeSelected, dateSelected) > new Date().getTime()) {
                            object.splice(index, 1);
                            sortMatches({ ...match });
                        } else {
                            ongoingMatches.sort((a, b) => getTimeOfString(a.time) - getTimeOfString(b.time));
                        }
                    }
                });
            } else {
                let firstTeam = { ...props.match.firstTeam, ...getItemFromDictByValue(teams, "name", selectedLeftTeam) };
                let secondTeam = { ...props.match.secondTeam, ...getItemFromDictByValue(teams, "name", selectedRightTeam) };

                let picksList = props.match.picks;
                for (let i = 0; i < picksList.length; ++i) {
                    if (picksList[i].team !== null) {
                        if (picksList[i].team === props.match.firstTeam.name)
                            picksList[i].team = firstTeam.name;
                        else
                            picksList[i].team = secondTeam.name;
                    }
                }

                props.setMatch(
                    {
                        ...props.match,
                        matchDate: stringDateToFullDate(),
                        event: eventSelected,
                        firstTeam: firstTeam,
                        secondTeam: secondTeam,
                        picks: picksList
                    }
                )
            }

            try {
                await request("POST", `editMatchInfo/${valueEvent}/${matchId}`,
                    {
                        date: dateSelected,
                        time: timeSelected,
                        event: eventSelected,
                        nameFirst: selectedLeftTeam,
                        nameSecond: selectedRightTeam
                    });

                showNotification("Матч успешно изменён", "ok");
                setMatchEditorActive(false);
            } catch (err) {
                showNotification(err.response.data.message, "warn");
            }
        }
    }


    return (
        <div>
            <div className="header_splash_window">
                <div className="logo_splash_window"></div>
            </div>
            <div className="info_text">
                <p>Укажите информацию о матче</p>
            </div>
            <div className="col_center_gap30">
                <div className="col_center_gap10">
                    <DefaultSelector issuer={"datePicker"} type={"datePicker"} styleMain={{ zIndex: 3 }} toggleClass={toggleDate} value={dateSelected} startValue={valueDate} setValue={setDateSelected} selectorActive={dateSelectorActive} minDate={parseDateString(valueDate)} maxDate={genMaxDate()} timeSelected={timeSelected} setTimeSelected={setTimeSelected} valueTime={valueTime} />
                    <div className="row_center_6" style={{ zIndex: 2 }}>
                        <div className="text-field_half">
                            <DefaultSelector issuer={"timePicker"} type={"half"} styleMain={null} styleItems={null} toggleClass={toggleTime} value={timeSelected} setValue={setTimeSelected} startValue={dateSelected === valueDate ? valueTime : "00:00"} selectorActive={timeSelectorActive} />
                        </div>
                        {allEvents !== null ?
                            <SearchableSelector issuer={"eventSelector"} type={"half"} styleMain={null} styleItems={null} setSelectorActive={setEventSelectorActive} value={eventSelected} startValue={valueEvent} selectorActive={eventSelectorActive} data={allEvents} setValue={setEventSelected} itemKey={"name"} srcKey={"eventSrc"} />
                            :
                            <></>
                        }
                    </div>
                    {teams !== null && teamsActive !== null ?
                        <div className="row_center_6" style={{ zIndex: 1 }}>
                            <SearchableArraySelector issuer={"eventTeamSelector"} styleMain={null} toggleClass={toggleLeftTeam} value={selectedLeftTeam} startValue={valueLeftTeam} selectorActive={leftTeamSelectorActive} data={teams} arrayActive={teamsActive} itemKey={"name"} srcKey={"logo"} setDataValue={setSelectedLeftTeam} setItem={setTeam} type={"half"} />
                            <SearchableArraySelector issuer={"eventTeamSelector"} styleMain={null} toggleClass={toggleRightTeam} value={selectedRightTeam} startValue={valueRightTeam} selectorActive={rightTeamSelectorActive} data={teams} arrayActive={teamsActive} itemKey={"name"} srcKey={"logo"} setDataValue={setSelectedRightTeam} setItem={setTeam} type={"half"} />
                        </div>
                        :
                        <></>
                    }
                </div>
                <div className="col_center_gap_20">
                    <div className="full_grey_button">
                        <input type="submit" value="Подтвердить" onClick={() => { onAccept() }} />
                    </div>
                    <div className="leave_tournament_button display-row-center" style={{ margin: 0, width: "198px", height: "38px", padding: 0 }} onClick={() => { setMatchEditorActive(false); setDeleteMatchActive(true) }}>
                        <p style={{ margin: 0, fontSize: "14px" }}>Удалить матч</p>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default SetMatchInfo;