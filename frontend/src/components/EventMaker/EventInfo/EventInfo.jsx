import React from "react";
import { useState, useEffect } from 'react';
import Participant from '../../Participant/Participant';
import PrizePlace from '../../PrizePlace/PrizePlace';
import Map from '../../Map/Map';
import Editor from "../../Editor/Editor";
import Login from "../../Login/Login";
import './EventInfo.css';
import '../../Participant/Participant.css';
import SearchableArraySelector from "../../Selector/SearchableArraySelector";
import SearchableSelector from "../../Selector/SearchableSelector";
import NonSelectableSelector from "../../Selector/NonSelectableSelector";
import PrizePlaceGenerator from "../../PrizePlace/PrizePlaceGenerator";
import MapPoolGenerator from "../../Map/MapPoolGenerator";
import PlayerLogoWrapper from "../../PlayerWrapper/PlayerLogoWrapper";
import { request } from "../../../Utils/MyAxios";
import { getPrizePlaceEnding, indexOf, isDuplicatedPlacesExists, isEmpty, isWrongPrizePlacesOrder, isNotInList, getItemFromDictByValue, showNotification } from "../../../Utils/Utils";
import { Link } from "react-router-dom";


function EventInfo(props) {
    const [setedPrizePlaces, setSetedPrizePlaces] = useState(null);
    const [setedPrizes, setSetedPrizes] = useState(null);

    const [valueSetedPrizePlaces, setValueSetedPrizePlaces] = useState(null);
    const [valueSetedPrizes, setValueSetedPrizes] = useState(null);

    const [diskLinkEditorActive, setDiskLinkEditorActive] = useState(false); //состояния модального окна для редактирования

    const [prizePlaceEditorActive, setPrizePlaceEditorActive] = useState(false); //состояния модального окна для редактирования призовых мест
    const [prizePlaceSetterActive, setPrizePlaceSetterActive] = useState(false); //состояния модального окна для редактирования призовых мест

    const [descriptionSetterActive, setDescriptionSetterActive] = useState(false); //состояния модального окна для редактирования призовых мест

    const [mapsSetterActive, setMapsSetterActive] = useState(false); //состояния модального окна для редактирования призовых мест

    const [selectorActive, setSelectorActive] = useState(null); // состояния селектора

    const [mvpSelectorActive, setMvpSelectorActive] = useState(false); // состояния селектора
    const [mvpWindowActive, setMvpWindowActive] = useState(false);
    const [mvpValue, setMvpValue] = useState("Выберите игрока");

    const [valueTeam, setValueTeam] = useState(null); //Для селектора команды
    const [teamsActive, setTeamsActive] = useState(null); // состояния команд - выбрана ли команда(чтоб блокировать ее)

    const [valueMap, setValueMap] = useState(null); //Для селектора команды
    const [selectedValueMap, setSelectedValueMap] = useState(null); //Для селектора команды
    const [selectorMapActive, setSelectorMapActive] = useState(null); // состояния селектора

    const [mapsActive, setMapsActive] = useState(null); // состояния команд - выбрана ли команда(чтоб блокировать ее)

    const [selectedDiskLink, setSelectedDiskLink] = useState("");
    const [selectedDescription, setSelectedDesctription] = useState("");

    const [valueDiskLink, setValueDiskLink] = useState(props.tournament.yaDiskUrl); //Для селектора команды


    useEffect(() => {
        initPrizePlaces();

        setSelectedDesctription(props.tournament.description);
        setSelectedDiskLink(props.tournament.yaDiskUrl);

        initMapPool();

        if (props.tournament.prizePlaces !== undefined && props.tournament.prizePlaces !== null) {
            setSelectorActive(Array(props.tournament.prizePlaces.length).fill(false));
            setValueTeam(Array(props.tournament.prizePlaces.length).fill("Выберите команду"));
            setTeamsActive(Array(props.tournament.prizePlaces.length).fill(false));
        }
    }, []);


    function initMapPool() {
        setValueMap(props.tournament.mapPool);
        setSelectedValueMap(props.tournament.mapPool);

        setSelectorMapActive(Array(props.tournament.mapPool.length).fill(false))

        let tempMapActive = Array(props.eventMapPool.length).fill(false);

        props.tournament.mapPool.map((map) => {
            let idx = props.eventMapPool.indexOf(map);
            if (idx !== -1) {
                tempMapActive[idx] = true;
            }
        })
        setMapsActive(tempMapActive);
    }


    function initPrizePlaces() {
        let places = props.tournament.prizePlaces.map((place) => {
            return place.place;
        })

        let prizes = props.tournament.prizePlaces.map((place) => {
            return place.reward;
        });

        setSetedPrizePlaces(places);
        setSetedPrizes(prizes);

        setValueSetedPrizePlaces(places);
        setValueSetedPrizes(prizes)
    }


    function urlFix(url) {
        if (url.startsWith("https://")) {
            return url.replace("https://", "");
        }
        else if (url.startsWith("http://")) {
            return url.replace("http://", "");
        }
        return url;
    }


    function toggleClass(id, idx = null) { // функция toggle для селектора
        let temp = [];
        for (let i = 0; i < selectorActive.length; ++i) {
            if (idx !== null && i === id)
                temp.push(idx);
            else
                temp.push(false);
        }
        if (id !== "all" && idx === null) {
            temp[id] = !selectorActive[id];
        }
        setSelectorActive(temp);
    };


    function setTeam(id, value) { // с её помощью делаем нужную команду заблокированной 
        let temp = [...teamsActive];
        let val = indexOf(value, props.tournament.participants, "name");

        temp[val] = !temp[val]; // ресет
        temp[id] = !temp[id];   // новый
        setTeamsActive(temp);
    }


    function setTeamValue(id, value) { // ставим выбранную команду в выбранное поле
        let tempTeams = [...valueTeam];
        tempTeams[id] = value;
        setValueTeam(tempTeams);
    }


    function generateSelectors() {
        let prizePlaces = props.tournament.prizePlaces;
        const size = prizePlaces.length;
        let content = []
        for (let i = 0; i < size; ++i) {
            if (prizePlaces[i]["teamName"] !== "" && prizePlaces[i]["teamName"] !== null && valueTeam[i] === "Выберите команду") {
                setTeamValue(i, prizePlaces[i]["teamName"]);
                setTeam(indexOf(prizePlaces[i]["teamName"], props.tournament.participants, "teamName"), prizePlaces[i]["teamName"])
            }
            content.push(
                <div className="row_center_6" key={`prizePlacesNonRegistration_selector${i}`}>
                    <SearchableArraySelector issuer={"prizePlacesNonRegistration"} styleMain={{ zIndex: size - i }} toggleClass={toggleClass} value={valueTeam[i]} startValue={(prizePlaces[i]["teamName"] === null || prizePlaces[i]["teamName"] === "") ? "Выберите команду" : prizePlaces[i]["teamName"]} selectorActive={selectorActive[i]} data={props.tournament.participants} arrayActive={teamsActive} itemKey={"teamName"} setDataValue={setTeamValue} setItem={setTeam} srcKey={"logo"} index={i} type={"half"} />
                    <NonSelectableSelector type={"half"} styleMain={{ zIndex: size - i }} text={getPrizePlaceEnding(prizePlaces[i]["place"])} styleP={null} />
                </div>
            );
        }
        return content;
    }


    async function onDiskLinkEdited() {
        if (!(selectedDiskLink.startsWith("https://") || selectedDiskLink.startsWith("http://"))) {
            showNotification("Ссылка должна содержать протокол http/https", "warn");
        } else {
            try {
                await request("POST", `editDiskUrl/${props.tournament.event}`, selectedDiskLink);
                setValueDiskLink(selectedDiskLink);
                setDiskLinkEditorActive(false);
                showNotification("Ссылка успешно изменена", "ok");
            } catch (err) {
                showNotification(err.response.data.message, "warn");
            }
        }
    };


    function toggleMap(id) { // функция toggle для селектора
        let temp = [];
        for (let i = 0; i < selectorMapActive.length; ++i) {
            temp.push(false);
        }
        if (id !== "all") {
            temp[id] = !selectorMapActive[id];
        }
        setSelectorMapActive(temp);
    };


    function isChanged(data1, data2) {
        if (data1 === null || data2 === null) {
            return true;
        }

        if (data1.length !== data2.length) {
            return false;
        }

        let result = data1.map((item, index) => {
            return item === data2[index];
        })

        return !result.includes(false);
    }


    async function onRegistrationPrizePlaceEdited() {
        let places = props.tournament.prizePlaces.map((place) => {
            return place.place;
        })

        let prizes = props.tournament.prizePlaces.map((place) => {
            return place.reward;
        });

        if (isChanged(places, setedPrizePlaces) && isChanged(prizes, setedPrizes)) {
            showNotification("Вы ничего не изменили", "neutral");
        } else if (isWrongPrizePlacesOrder(setedPrizePlaces)) {
            showNotification("Вы указали неправильный порядок призовых мест", "warn");
        } else if (isDuplicatedPlacesExists(setedPrizePlaces)) {
            showNotification("Вы указали призовые места через тире, но их количество некорректно", "warn");
        } else if (isEmpty(setedPrizes)) {
            showNotification("Вы не указали призы", "warn");
        } else {
            let prizePlaces_ = setedPrizePlaces.map((place, index) => {
                return { place: place, reward: setedPrizes[index], teamName: "", src: "" }
            });
            try {
                await request("POST", `editPrizePlaces/${props.tournament.event}`, prizePlaces_);
                props.setTournament({
                    ...props.tournament,
                    prizePlaces: prizePlaces_
                });
                showNotification("Призовые места успешно изменены", "ok");
                setPrizePlaceSetterActive(false);
            } catch (err) {
                showNotification(err.response.data.message, "warn")
            }
        }
    }


    async function onDescriptionEdited() {
        if (props.tournament.description === selectedDescription) {
            showNotification("Вы ничего не изменили", "neutral");
        } else {
            try {
                await request("POST", `editEventDescription/${props.tournament.event}`, selectedDescription);
                props.setTournament({
                    ...props.tournament,
                    description: selectedDescription
                });

                showNotification("Описание успешно изменено", "ok");

                setDescriptionSetterActive(false)
            } catch (err) {
                showNotification(err.response.data.message, "warn")
            }
        }
    }


    async function onMapPoolEdited() {
        let maps = props.tournament.mapPool.map((map) => {
            return map;
        })

        if (isChanged(selectedValueMap, maps)) {
            showNotification("Вы ничего не изменили", "neutral");
        } else if (isEmpty(selectedValueMap)) {
            showNotification("Вы не указали карты в пуле", "warn");
        } else {
            try {
                await request("POST", `editEventMapPool/${props.tournament.event}`, selectedValueMap);
                props.setTournament({
                    ...props.tournament,
                    mapPool: selectedValueMap
                });
                showNotification("Пул успешно изменён", "ok");
                setMapsSetterActive(false);
            } catch (err) {
                showNotification(err.response.data.message, "warn")
            }
        }
    }


    function getItemForPrizePlace(teamName) {
        if (teamName === "Выберите команду") {
            return { src: "" };
        }
        const { name, status, type, ...rest } = getItemFromDictByValue(props.tournament.participants, "teamName", teamName);

        return rest;
    }


    async function onPrizePlaceSetted() {
        let prizePlaces = props.tournament.prizePlaces;
        let res = prizePlaces.map((place, index) => {
            if (place.teamName === "") {
                return "Выберите команду" === valueTeam[index];
            }
            return place.teamName === valueTeam[index];
        });

        let filteredTeams = valueTeam.filter(team => team !== "Выберите команду" && team !== "").map((team) => {
            return isNotInList(props.tournament.participants, "teamName", team)
        });

        if (!res.includes(false)) {
            showNotification("Вы ничего не изменили", "neutral");
        } else if (filteredTeams.includes(true)) {
            showNotification("Одной из указанных команд не существует", "warn");
        } else {
            try {
                let placesToSet = prizePlaces.map((place, index) => ({
                    place: place.place,
                    reward: place.reward,
                    teamName: valueTeam[index] === "Выберите команду" ? "" : valueTeam[index],
                    ...getItemForPrizePlace(valueTeam[index])
                }));

                await request("POST", `editPrizePlaces/${props.tournament.event}`, placesToSet);

                props.setTournament({
                    ...props.tournament,
                    prizePlaces: placesToSet
                });
                showNotification("Призовые места успешно изменены", "ok");
                setPrizePlaceEditorActive(false);
            } catch (err) {
                showNotification(err.response.data.message, "warn");
            }
        }
    }


    function onOpenRegistrationPrizePlaceEditor() {
        initPrizePlaces();
        setPrizePlaceSetterActive(true);
    }


    function onOpenEditPricePlacesEditor() {
        toggleClass("all");
        let prizePlaces = props.tournament.prizePlaces;
        let tempArr = [];
        for (let i = 0; i < prizePlaces.length; ++i) {
            tempArr.push((prizePlaces[i]["teamName"] === null || prizePlaces[i]["teamName"] === "") ? "Выберите команду" : prizePlaces[i]["teamName"]);
        }
        setValueTeam(tempArr);
        setPrizePlaceEditorActive(true);
    }


    function onMvpWindowOpen() {
        setMvpSelectorActive(false); // состояния селектора
        setMvpValue(isEmpty(props.mvp, "str") ? "Выберите игрока" : props.mvp);
        setMvpWindowActive(true);
    }


    function getMvpByName(name) {
        if (name === "" || name === null)
            return null;
        let mvp = getItemFromDictByValue(props.tournament.teamsPlayers, "nick", name);
        if (mvp === undefined)
            return null;
        return (
            <div className="tba_participant">
                <Link to={`/player/${name}`} style={{ textDecoration: "none" }}>
                    <PlayerLogoWrapper issuer={"participant"} teamSrc={mvp.teamSrc} team={mvp.team} playerSrc={mvp.src} player={name} />
                </Link>
            </div>
        );
    }


    async function setEventMvp() {
        try {
            if (mvpValue === props.mvp)
                showNotification("Вы ничего не изменили", "neutral");
            else {
                await request("POST", `editEventMVP/${props.tournament.event}/${mvpValue}`, {});
                props.setEventMVP(mvpValue);
                showNotification("MVP турнира успешно изменён", "ok");
                setMvpWindowActive(false);
            }
        } catch (err) {
            showNotification(err.response.data.message, "warn");
        }
    }


    return (
        <div>
            <div className="space_wrapper">
                <div className="container_name">
                    <div className="event_col"><p>{props.part_header}</p></div>
                    <div className="participants_wrapper">
                        <div className="item">
                            {props.tournament.participants.map((participant =>
                                <Participant part={participant} allParts={props.tournament.participants} tournament={props.tournament} setTournament={props.setTournament} isAdmin={props.isAdmin} status={props.status} key={participant.teamName} />
                            ))}
                            {props.status === "upcoming" ? <Participant partLength={props.tournament.participants.length} total={props.tournament.total} /> : <></>}
                        </div>
                    </div>
                </div>

                {(isEmpty(props.mvp, "str") || props.isAdmin) && props.status === "ongoing" ?
                    <div className="container_name">
                        <div className="row_center_5px">
                            <div className="event_col"><p>MVP турнира</p></div>
                            {props.isAdmin ? <Editor size="12px" depth={2} onClick={() => onMvpWindowOpen()} /> : <></>}
                        </div>
                        {getMvpByName(props.mvp)}
                    </div>
                    :
                    <></>
                }

                <div className="container_name">
                    <div className="row_center_5px">
                        <div className="event_col"><p>Призовые места</p></div>
                        {props.isAdmin ? <Editor size="12px" depth={2} onClick={() => props.status === "upcoming" ? onOpenRegistrationPrizePlaceEditor() : onOpenEditPricePlacesEditor()} /> : <></>}
                    </div>
                    <div className="participants_wrapper">
                        <PrizePlace prize={props.tournament.prizePlaces} />
                    </div>
                </div>

                <div className="event_info_bottom">
                    <div className="container_name">
                        <div className="row_center_5px">
                            <div className="event_col">
                                <p>Пул карт</p>
                            </div>
                            {props.isAdmin && props.status === "upcoming" && props.eventMapPool !== null ? <Editor size="12px" depth={2} onClick={() => { initMapPool(); setMapsSetterActive(true) }} /> : <></>}
                        </div>
                        <div className="maps">
                            {props.tournament.mapPool.map((map_name) =>
                                <div className="map_wrapper" key={map_name}>
                                    <Map map={map_name} maps={props.eventMapPool} />
                                    <div className="map_name_wrapper"><p>{map_name}</p></div>
                                </div>
                            )}
                        </div>
                    </div>

                    <div className="event_info_bottom_wrapper">
                        {props.tournament.description !== "" || props.isAdmin ?
                            <div className="container_name">
                                <div className="row_center_5px">
                                    <div className="event_col">
                                        <p>Формат игр</p>
                                    </div>
                                    {props.isAdmin && props.status === "upcoming" ? <Editor size="12px" depth={2} onClick={() => { setSelectedDesctription(props.tournament.description); setDescriptionSetterActive(true) }} /> : <></>}
                                </div>
                                <div className="event_info">
                                    <p>{props.tournament.description}</p>
                                </div>
                            </div>
                            :
                            <></>
                        }
                        {valueDiskLink !== "" || props.isAdmin ?
                            <div className="container_name">
                                <div className="row_center_5px">
                                    <div className="event_col"><p>Архив с фотографиями</p></div>
                                    {props.isAdmin ? <Editor size="12px" depth={2} onClick={() => { setSelectedDiskLink(props.tournament.yaDiskUrl); setDiskLinkEditorActive(true) }} /> : <></>}
                                </div>
                                <div className="event_info">
                                    <a href={valueDiskLink} target="_blank" rel="noopener noreferrer">{urlFix(valueDiskLink)}</a>
                                </div>
                            </div>
                            :
                            <></>
                        }
                    </div>
                </div>
            </div>

            {/* Вызывается когда турнир в состоянии регистрации */}
            {
                props.isAdmin ?
                    <Login active={prizePlaceSetterActive} setActive={setPrizePlaceSetterActive}>
                        <div className="header_splash_window" onClick={() => toggleClass("all")}>
                            <div className="logo_splash_window"></div>
                        </div>
                        <div className="info_text" onClick={() => toggleClass("all")}>
                            <p>Укажите информацию о призовых местах</p>
                        </div>
                        <div className="col_center_gap30">
                            <div className="inside" style={{ overflowY: "auto" }}>
                                <PrizePlaceGenerator
                                    setedPrizes={setedPrizes}
                                    setSetedPrizes={setSetedPrizes}
                                    setedPrizePlaces={setedPrizePlaces}
                                    setSetedPrizePlaces={setSetedPrizePlaces}
                                    setedValuePrizePlaces={valueSetedPrizePlaces}
                                    setValueSetedPrizePlaces={setValueSetedPrizePlaces}
                                    setedValuePrizes={valueSetedPrizes}
                                    setValueSetedPrizes={setValueSetedPrizes}
                                    selectedMaxTeams={props.tournament.total}
                                />
                            </div>
                            <div className="full_grey_button">
                                <input type="submit" value="Сохранить" onClick={() => { onRegistrationPrizePlaceEdited() }} />
                            </div>
                        </div>
                    </Login>
                    :
                    <></>
            }

            {/* Вызывается когда турнир идёт */}
            {
                props.isAdmin ?
                    <Login active={prizePlaceEditorActive} setActive={setPrizePlaceEditorActive}>
                        <div className="header_splash_window" onClick={() => toggleClass("all")}>
                            <div className="logo_splash_window"></div>
                        </div>
                        <div className="info_text" onClick={() => toggleClass("all")}>
                            <p>Укажите информацию о призовых местах</p>
                        </div>
                        <div className="col_center_gap30">
                            <div className="inside" style={{ overflowY: props.tournament.prizePlaces.length > 5 ? "auto" : null }}>
                                {selectorActive !== null && valueTeam !== null && teamsActive !== null ?
                                    generateSelectors()
                                    :
                                    <></>}
                            </div>
                            <div className="full_grey_button">
                                <input type="submit" value="Сохранить" onClick={() => { onPrizePlaceSetted() }} />
                            </div>
                        </div>
                    </Login>
                    :
                    <></>
            }

            {
                props.isAdmin ?
                    <Login active={mapsSetterActive} setActive={setMapsSetterActive}>
                        <div className="header_splash_window" >
                            <div className="logo_splash_window"></div>
                        </div>
                        <div className="info_text">
                            <p>Укажите информацию о пуле карт</p>
                        </div>
                        <div className="col_center_gap30">
                            <MapPoolGenerator
                                valueMap={valueMap}
                                selectorMapActive={selectorMapActive}
                                mapsActive={mapsActive}
                                eventMapPool={props.eventMapPool}
                                toggleMap={toggleMap}
                                selectedValueMap={selectedValueMap}
                                setSelectedValueMap={setSelectedValueMap}
                                setMapsActive={setMapsActive}
                                setValueMap={setValueMap}
                                setSelectorMapActive={setSelectorMapActive}
                            />
                            <div className="col_center_gap_20">
                                <div className="full_grey_button">
                                    <input type="submit" value="Подтвердить" onClick={() => { onMapPoolEdited() }} />
                                </div>
                            </div>
                        </div>
                    </Login>
                    :
                    <></>
            }

            {
                props.isAdmin ?
                    <Login active={diskLinkEditorActive} setActive={setDiskLinkEditorActive}>
                        <div className="header_splash_window">
                            <div className="logo_splash_window"></div>
                        </div>
                        <div className="info_text">
                            <p>Укажите ссылку на диск с фото</p>
                        </div>
                        <div className="col_center_gap30">
                            <div className="text-field">
                                <input className="text-field_input" type="text" name="diskLink" placeholder="Введите ссылку на диск с фото" style={{ color: selectedDiskLink === props.tournament.yaDiskUrl ? "var(--white70)" : "white" }} onChange={e => { setSelectedDiskLink(e.target.value) }} value={selectedDiskLink} />
                            </div>
                            <div className="full_grey_button">
                                <input type="submit" value="Сохранить" onClick={() => { onDiskLinkEdited() }} />
                            </div>
                        </div>
                    </Login>
                    :
                    <></>
            }

            {
                props.isAdmin ?
                    <Login active={descriptionSetterActive} setActive={setDescriptionSetterActive}>
                        <div className="header_splash_window">
                            <div className="logo_splash_window"></div>
                        </div>
                        <div className="info_text">
                            <p>Укажите информацию о формате игр</p>
                        </div>
                        <div className="col_center_gap30">
                            <div className="description_field">
                                <textarea type="text" placeholder="Введите описание" style={{ width: "434px", color: selectedDescription === props.tournament.description ? "var(--white70)" : "white", fontSize: "16px", height: "65px" }} onChange={e => { setSelectedDesctription(e.target.value) }} value={selectedDescription} />
                            </div>
                            <div className="full_grey_button">
                                <input type="submit" value="Сохранить" onClick={() => { onDescriptionEdited() }} />
                            </div>
                        </div>
                    </Login>
                    :
                    <></>
            }

            {
                props.isAdmin ?
                    <Login active={mvpWindowActive} setActive={setMvpWindowActive}>
                        <div className="header_splash_window">
                            <div className="logo_splash_window"></div>
                        </div>
                        <div className="info_text">
                            <p>Выберите игрока, который станет MVP турнира</p>
                        </div>
                        {props.tournament.teamsPlayers !== null ?
                            <div className="col_center_gap30">
                                <SearchableSelector issuer={"eventSelector"} type={"half"} styleMain={{ zIndex: 2 }} styleItems={null} setSelectorActive={setMvpSelectorActive} value={mvpValue} startValue={mvpValue === "Выберите игрока" ? "Выберите игрока" : props.mvp} selectorActive={mvpSelectorActive} data={props.tournament.teamsPlayers} setValue={setMvpValue} itemKey={"nick"} srcKey={"src"} />
                                {getItemFromDictByValue(props.tournament.teamsPlayers, "nick", mvpValue) !== undefined ?
                                    <div className="full_grey_button">
                                        <input type="submit" value="Подтвердить" onClick={() => { setEventMvp() }} />
                                    </div>
                                    :
                                    <></>
                                }
                            </div>
                            :
                            <></>
                        }
                    </Login>
                    :
                    <></>
            }
        </div >
    );
}

export default EventInfo;