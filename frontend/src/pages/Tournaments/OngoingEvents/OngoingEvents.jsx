import React, { useState } from "react";
import OngoingEventsMaker from "../../../components/EventMaker/OngoingEventsMaker/OngoingEventsMaker";
import Editor from "../../../components/Editor/Editor";
import Login from "../../../components/Login/Login";
import './OngoingEvents.css';
import "../../../components/EventMaker/EventInfo/EventInfo.css";
import SearchableSelector from "../../../components/Selector/SearchableSelector";
import SearchableArraySelector from "../../../components/Selector/SearchableArraySelector";
import NonSelectableSelector from "../../../components/Selector/NonSelectableSelector";
import DefaultSelector from "../../../components/Selector/DefaultSelector";
import FileLoader from "../../../components/FileLoader/FileLoader";
import FullEventPopUp from "../../../components/FullEventPopUp/FullEventPopUp";
import { request } from "../../../Utils/MyAxios";
import { findKeyByName, fixNumbers, genMaxDate, getItemFromDictByValue, getMonthName, isNotInList, parseDateString, showNotification, onImageUploaded, isWrongPrizePlacesOrder, isDuplicatedPlacesExists, isEmpty, getPrizePlaceEnding } from "../../../Utils/Utils";
import Preloader from "../../../components/Preloader/Preloader";

function OngoingEvents(props) {
    const [eventChooserActive, setEventChooserActive] = useState(false);
    const [eventEditorActive, setEventEditorActive] = useState(false);
    const [chooseEventActive, setChooseEventActive] = useState(false);
    const [chooseOngoingEventActive, setChooseOngoingEventActive] = useState(false);
    const [editOngoingEventActive, setEditOngoingEventActive] = useState(false);

    const [deleteEventActive, setDeleteEventActive] = useState(false);
    const [createEventActive, setCreateEventActive] = useState(false);

    const [dateSelected, setDateSelected] = useState('Выберите дату начала'); // здесь хранится выбраная дата
    const [dateEndSelected, setDateEndSelected] = useState('Выберите дату окончания'); // здесь хранится выбраная дата
    const [eventSelected, setEventSelected] = useState('Выберите турнир'); // здесь хранится выбраный турнир

    const [valueStartDate, setValueStartDate] = useState(''); // Это для даты выбранного матча
    const [valueEndDate, setValueEndDate] = useState(''); // Это для даты выбранного матча

    const [dateSelectorActive, setDateSelectorActive] = useState(false); // открыт/закрыт календарь
    const [dateEndSelectorActive, setDateEndSelectorActive] = useState(false); // открыт/закрыт календарь
    const [eventSelectorActive, setEventSelectorActive] = useState(false);
    const [countrySelectorActive, setCountrySelectorActive] = useState(false);
    const [citySelectorActive, setCitySelectorActive] = useState(false);

    const [citySelected, setCitySelected] = useState('Выберите город'); // здесь хранится выбраный турнир
    const [valueCity, setValueCity] = useState('Выберите город'); //Для селектора страны

    const [countrySelected, setCountrySelected] = useState('Выберите страну'); // здесь хранится выбраный турнир
    const [valueCountry, setValueCountry] = useState('Выберите страну'); //Для селектора страны

    const [valuePrize, setValuePrize] = useState(''); //Для селектора страны
    const [valueFee, setValueFee] = useState(''); //Для селектора страны
    const [selectedPrize, setSelectedPrize] = useState('Укажите приз'); //Для селектора страны
    const [selectedFee, setSelectedFee] = useState('Укажите взнос'); //Для селектора страны

    const [selectedFormat, setSelectedFormat] = useState('Выберите формат'); // формат (bo1 и тп)
    const [valueFormat, setValueFormat] = useState('Выберите формат'); // формат (bo1 и тп)
    const [formatSelectorActive, setFormatSelectorActive] = useState(false);

    const [selectedType, setSelectedType] = useState('Выберите тип'); // тип (Лан/Онлайн)
    const [valueType, setValueType] = useState('Выберите тип'); // тип (Лан/Онлайн)
    const [typeSelectorActive, setTypeSelectorActive] = useState(false);

    const [valueLogoPath, setValueLogoPath] = useState({ path: "Укажите путь до логотипа", file: null });
    const [selectedLogoPath, setSelectedLogoPath] = useState({ path: "Укажите путь до логотипа", file: null });

    const [valueHeaderPath, setValueHeaderPath] = useState({ path: "Укажите путь до шапки", file: null });
    const [selectedHeaderPath, setSelectedHeaderPath] = useState({ path: "Укажите путь до шапки", file: null });

    const [valueTrophyPath, setValueTrophyPath] = useState({ path: "Укажите путь до трофея", file: null });
    const [selectedTrophyPath, setSelectedTrophyPath] = useState({ path: "Укажите путь до трофея", file: null });

    const [valueMvpPath, setValueMvpPath] = useState({ path: "Укажите путь до MVP", file: null });
    const [selectedMvpPath, setSelectedMvpPath] = useState({ path: "Укажите путь до MVP", file: null });

    const [valueDecription, setValueDecription] = useState(''); // тип (Лан/Онлайн)
    const [selectedValueDecription, setSelectedValueDecription] = useState(''); // тип (Лан/Онлайн)

    const [valueName, setValueName] = useState('');
    const [selectedName, setSelectedName] = useState('Укажите название');

    const [valueDiskLink, setValueDiskLink] = useState('Укажите ссылку на диск с фото'); // тип (Лан/Онлайн)
    const [selectedDiskLink, setSelectedDiskLink] = useState('Укажите ссылку на диск с фото'); // тип (Лан/Онлайн)

    const [valueMaxTeams, setValueMaxTeams] = useState(''); // тип (Лан/Онлайн)
    const [selectedMaxTeams, setSelectedMaxTeams] = useState(''); // тип (Лан/Онлайн)

    const [valueMap, setValueMap] = useState(null); //Для селектора команды
    const [selectedValueMap, setSelectedValueMap] = useState(null); //Для селектора команды

    const [selectorMapActive, setSelectorMapActive] = useState(null); // состояния селектора

    const [mapsActive, setMapsActive] = useState(null); // состояния команд - выбрана ли команда(чтоб блокировать ее)

    const [setedPrizePlaces, setSetedPrizePlaces] = useState(null);
    const [setedPrizes, setSetedPrizes] = useState(null);

    const [setedValuePrizePlaces, setValueSetedPrizePlaces] = useState(null);
    const [setedValuePrizes, setValueSetedPrizes] = useState(null);

    const [selectorActive, setSelectorActive] = useState(null); // состояния селектора
    const [valueTeam, setValueTeam] = useState(null); //Для селектора команды
    const [teamsActive, setTeamsActive] = useState(null); // состояния команд - выбрана ли команда(чтоб блокировать ее)

    const [teams, setTeams] = useState(null);


    function toggleDate() {
        setDateSelectorActive(!dateSelectorActive);
        setDateEndSelectorActive(false);
    };


    function toggleEndDate() {
        setDateEndSelectorActive(!dateEndSelectorActive);
        setDateSelectorActive(false);
    };



    function toggleFormat() {
        setFormatSelectorActive(!formatSelectorActive);
    }


    function toggleType() {
        setTypeSelectorActive(!typeSelectorActive);
    }


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


    function togglePrizePlace(id) { // функция toggle для селектора
        let temp = [];
        for (let i = 0; i < selectorActive.length; ++i) {
            temp.push(false);
        }
        if (id !== "all") {
            temp[id] = !selectorActive[id];
        }
        setSelectorActive(temp);
    };


    function setTeam(id, value) { // с её помощью делаем нужную команду заблокированной 
        let temp = [...teamsActive];
        let val = indexOf(value);

        temp[val] = !temp[val];
        temp[id] = !temp[id];
        setTeamsActive(temp);
    }


    function setTeamValue(id, value) { // ставим выбранную команду в выбранное поле
        let tempTeams = [...valueTeam];
        tempTeams[id] = value;
        setValueTeam(tempTeams);
    }


    function indexOf(value) {
        for (let i = 0; i < teams.length; ++i) {
            if (value === teams[i].name) {
                return i;
            }
        }
    }


    function generatePrizePlacesSelectors() {
        let content = []
        let varprizePlaces = getItemFromDictByValue(props.ongoing, "event", valueName)?.prizePlaces;
        let size = varprizePlaces.length;

        for (let i = 0; i < size; ++i) {
            if (varprizePlaces[i]["teamName"] !== "" && varprizePlaces[i]["teamName"] !== null && valueTeam[i] === "Выберите команду") {
                setTeamValue(i, varprizePlaces[i]["teamName"]);
                setTeam(indexOf(varprizePlaces[i]["teamName"]), valueTeam[i])
            }
            content.push(
                <div className="row_center_6" key={`prizePlaceSelector${i}`}>
                    <SearchableArraySelector issuer={"prizePlacesNonRegistration"} styleMain={{ zIndex: size - i }} toggleClass={togglePrizePlace} value={valueTeam[i]} startValue={varprizePlaces[i]["teamName"] === null ? "Выберите команду" : varprizePlaces[i]["teamName"]} selectorActive={selectorActive[i]} data={teams} arrayActive={teamsActive} itemKey={"name"} setDataValue={setTeamValue} setItem={setTeam} srcKey={"src"} index={i} type={"half"} />
                    <NonSelectableSelector type={"half"} styleMain={{ zIndex: size - i }} text={getPrizePlaceEnding(varprizePlaces[i]["place"])} styleP={null} />
                </div>
            );
        }
        return content;
    }


    function setMatchInfo() {
        let choosedEvent = getItemFromDictByValue(props.ongoing, "event", eventSelected);
        setValuePrize(choosedEvent.prize);
        setSelectedPrize(choosedEvent.prize);

        setSelectedFee(choosedEvent.fee);
        setValueFee(choosedEvent.fee);

        setSelectedName(eventSelected);
        setValueName(eventSelected);

        setValueDiskLink(choosedEvent.yaDiskUrl);
        setSelectedDiskLink(choosedEvent.yaDiskUrl);

        setValueLogoPath({ path: choosedEvent.eventFile, file: choosedEvent.eventSrc });
        setSelectedLogoPath({ path: choosedEvent.eventFile, file: choosedEvent.eventSrc });

        setValueHeaderPath({ path: choosedEvent.headerFile, file: choosedEvent.headerSrc });
        setSelectedHeaderPath({ path: choosedEvent.headerFile, file: choosedEvent.headerSrc });

        setValueTrophyPath({ path: choosedEvent.trophyFile, file: null });
        setSelectedTrophyPath({ path: choosedEvent.trophyFile, file: null });

        setValueMvpPath({ path: choosedEvent.mvpFile, file: null });
        setSelectedMvpPath({ path: choosedEvent.mvpFile, file: null });

        setSelectorActive(Array(choosedEvent.prizePlaces.length).fill(false));

        setValueTeam(Array(choosedEvent.prizePlaces.length).fill('Выберите команду'));

        setTeamsActive(Array(choosedEvent.prizePlaces.length).fill(false));

        setTeams(choosedEvent.teams);

        if (selectorActive !== null)
            togglePrizePlace("all");
    }


    function setCreateMatchInfo() {
        setValueStartDate("Выберите дату начала");
        setDateSelected("Выберите дату начала");

        setValueEndDate("Выберите дату окончания");
        setDateEndSelected("Выберите дату окончания");

        setValueCountry("Выберите страну");
        setCountrySelected("Выберите страну");

        setCitySelected("Выберите город");
        setValueCity("Выберите город");

        setValuePrize("");
        setSelectedPrize("");

        setValueFee("");
        setSelectedFee("");

        setSelectedFormat("Выберите формат");
        setValueFormat("Выберите формат");

        setSelectedType("Выберите тип");
        setValueType("Выберите тип");

        setValueDecription("");
        setSelectedValueDecription("");

        setSelectedName("");
        setValueName("");

        setSelectedMaxTeams("");
        setValueMaxTeams("");

        setValueLogoPath({ path: "Укажите путь до логотипа", file: null });
        setSelectedLogoPath({ path: "Укажите путь до логотипа", file: null });

        setValueHeaderPath({ path: "Укажите путь до шапки", file: null });
        setSelectedHeaderPath({ path: "Укажите путь до шапки", file: null });

        setValueTrophyPath({ path: "Укажите путь до трофея", file: null });
        setSelectedTrophyPath({ path: "Укажите путь до трофея", file: null });

        setValueMvpPath({ path: "Укажите путь до MVP", file: null });
        setSelectedMvpPath({ path: "Укажите путь до MVP", file: null });

        setSetedPrizePlaces(null);
        setSetedPrizes(null);

        setValueSetedPrizePlaces(null);
        setValueSetedPrizes(null);

        setValueMap(null);
        setSelectedValueMap(null);

        setSelectorMapActive(null);

        setMapsActive(null);

        setDateEndSelectorActive(false);
        setDateSelectorActive(false);
        setCitySelectorActive(false);
        setCountrySelectorActive(false);
        setTypeSelectorActive(false);
        setFormatSelectorActive(false);
    }


    function getEventDateTime(eventDate) {
        let startDate = eventDate.split(" - ")[0];
        let splStartDate = startDate.split(".");
        return parseDateString(splStartDate.length === 2 ? `${startDate}.${new Date().getFullYear()}` : `${splStartDate[0]}.${splStartDate[1]}.${splStartDate[2].length === 2 ? `20${splStartDate[2]}` : splStartDate[2]}`).getTime();
    }


    function getEventsByDate() {
        if (props.featured !== null)
            return props.featured.flatMap(day => day.events).filter(event => getEventDateTime(event.date) === parseDateString(dateSelected).getTime())
        return [];
    }


    function handleImageUploaded(event, setSelectedFile) {
        let file = event.target.files[0];
        if (file !== null && file !== "null" && file !== "undefined" && file !== undefined) {
            if (file.size > 10485760) {
                showNotification("Размер фотографии не может быть больше 10мб", "warn");
            } else {
                setSelectedFile({ path: file.name, file: file });
            }
        }
    }


    async function onEventEdited() {
        let event = getItemFromDictByValue(props.ongoing, "event", valueName);
        let prizePlaces = event?.prizePlaces;
        let res = prizePlaces.map((place, index) => {
            if (place.teamName === "") {
                return "Выберите команду" === valueTeam[index];
            }
            return place.teamName === valueTeam[index];
        });

        let filteredTeams = valueTeam.filter(team => team !== "Выберите команду" && team !== "").map((team) => {
            return isNotInList(teams, "name", team)
        });

        if (selectedFee === valueFee && selectedPrize === valuePrize && selectedName === valueName &&
            selectedDiskLink === valueDiskLink && valueLogoPath.path === selectedLogoPath.path &&
            valueHeaderPath.path === selectedHeaderPath.path &&
            valueTrophyPath.path === selectedTrophyPath.path && valueMvpPath.path === selectedMvpPath.path &&
            !res.includes(false)) {
            showNotification("Вы ничего не изменили", "neutral");
        } else if (filteredTeams.includes(true)) {
            showNotification("Одной из указанных команд не существует", "warn");
        } else if (!selectedName.replace(/\s/g, '').length) {
            showNotification("Вы не указали название", "warn");
        } else if (!selectedPrize.replace(/\s/g, '').length) {
            showNotification("Вы не указали приз", "warn");
        } else if (!selectedFee.replace(/\s/g, '').length) {
            showNotification("Вы не указали взнос", "warn");
        } else {

            let placesToSet = prizePlaces.map((place, index) => ({
                ...place,
                teamName: valueTeam[index] === "Выберите команду" ? "" : valueTeam[index]
            }));

            let editedEvent = {
                ...event,
                fee: selectedFee,
                prize: selectedPrize,
                event: selectedName,
                yaDiskUrl: selectedDiskLink,
                headerFile: selectedHeaderPath.path,
                headerSrc: valueHeaderPath.path !== selectedHeaderPath.path ? URL.createObjectURL(selectedHeaderPath.file) : event.headerSrc,
                eventFile: selectedLogoPath.path,
                eventSrc: selectedLogoPath.path !== valueLogoPath.path ? URL.createObjectURL(selectedLogoPath.file) : event.eventSrc,
                trophyFile: selectedTrophyPath.path,
                trophySrc: selectedTrophyPath.file,
                mvpFile: selectedMvpPath.path,
                mvpSrc: selectedMvpPath.file,
                prizePlaces: placesToSet
            }

            try {
                await request("POST", `/editOngoingEvent/${valueName}`, editedEvent);

                if (selectedTrophyPath.file !== null) {
                    await onImageUploaded(selectedTrophyPath.file, "/events_trophy/", selectedName);
                }

                if (selectedHeaderPath.file !== null && !selectedHeaderPath.file.includes("blob")) {
                    await onImageUploaded(selectedHeaderPath.file, "/events_header/", selectedName);
                }

                if (selectedLogoPath.file !== null && !selectedLogoPath.file.includes("blob")) {
                    await onImageUploaded(selectedLogoPath.file, "/events_logo/", selectedName);
                }

                if (selectedMvpPath.file !== null) {
                    await onImageUploaded(selectedMvpPath.file, "/events_mvp/", selectedName);
                }

                props.setOngoingEvents(props.ongoing.map((event) => {
                    if (event.event === valueName) {
                        event = editedEvent;
                    }
                    return event;
                }))
                setEditOngoingEventActive(false);

                showNotification(`Турнир ${valueName} успешно изменён`, "ok");
            } catch (err) {
                showNotification(err.response.data.message, "warn")
            }
        }
    }


    async function onEventDelete() {
        try {
            await request("POST", `/deleteEvent/${valueName}`, {});

            if (getItemFromDictByValue(props.ongoing, "event", valueName) !== undefined) {
                props.ongoing.forEach(function (event, index, object) {
                    if (event.event === valueName) {
                        object.splice(index, 1);
                    }
                })
            } else {
                props.featured.forEach(function (day, index, object) {
                    day.events = day.events.filter(event => event.event !== valueName);

                    if (day.events.length === 0) {
                        object.splice(index, 1);
                    }
                });
            }
            showNotification(`Турнир ${valueName} успешно удалён`, "ok");
            setDeleteEventActive(false);
        } catch (err) {
            showNotification(err.response.data.message, "warn")
        }
    }


    function setOngoingMatchInfo(event) {
        let dates = event.date.split(" - ");
        let splStartDate = dates[0].split(".");
        let splEndDate = dates[1].split(".");

        let dateStart = `${dates[0]}.${new Date().getFullYear()}`;
        let dateEnd = `${dates[1]}.${new Date().getFullYear()}`;

        setValueStartDate(splStartDate.length === 2 ? dateStart : dates[0]);
        setDateSelected(splStartDate.length === 2 ? dateStart : dates[0]);

        setValueEndDate(splEndDate.length === 2 ? dateEnd : dates[1]);
        setDateEndSelected(splEndDate.length === 2 ? dateEnd : dates[1]);

        setValueCountry(event.country);
        setCountrySelected(event.country);

        setCitySelected(event.city);
        setValueCity(event.city);

        setValuePrize(event.prize);
        setSelectedPrize(event.prize);

        setValueFee(event.fee);
        setSelectedFee(event.fee);

        setSelectedFormat(event.format);
        setValueFormat(event.format);

        setSelectedType(event.type);
        setValueType(event.type);

        setValueDecription(event.description);
        setSelectedValueDecription(event.description);

        setSelectedName(event.event);
        setValueName(event.event);

        setSelectedMaxTeams(event.total);
        setValueMaxTeams(event.total);

        setValueLogoPath({ path: event.eventFile, file: null });
        setSelectedLogoPath({ path: event.eventFile, file: null });

        setValueHeaderPath({ path: event.headerFile, file: null });
        setSelectedHeaderPath({ path: event.headerFile, file: null });

        setValueTrophyPath({ path: event.trophyFile, file: null });
        setSelectedTrophyPath({ path: event.trophyFile, file: null });

        setValueMvpPath({ path: event.mvpFile, file: null });
        setSelectedMvpPath({ path: event.mvpFile, file: null });

        let eventPrizePlaces = event.prizePlaces;
        let places = eventPrizePlaces.map((place) => {
            return place.place;
        });
        let prizes = eventPrizePlaces.map((place) => {
            return place.reward;
        });

        setSetedPrizePlaces(places);
        setSetedPrizes(prizes);

        setValueSetedPrizePlaces(places);
        setValueSetedPrizes(prizes);

        let mapPool = event.mapPool;

        setValueMap(mapPool);
        setSelectedValueMap(mapPool);
        setSelectorMapActive(Array(mapPool.length).fill(false));

        let tempMapActive = Array(props.eventMapPool.length).fill(false);

        mapPool.map((map) => {
            let idx = props.eventMapPool.indexOf(map);
            if (idx !== -1) {
                tempMapActive[idx] = true;
            }
        })
        setMapsActive(tempMapActive);

        setDateEndSelectorActive(false);
        setDateSelectorActive(false);
        setCitySelectorActive(false);
        setCountrySelectorActive(false);
        setTypeSelectorActive(false);
        setFormatSelectorActive(false);

        if (selectorMapActive !== null)
            toggleMap("all");
    }


    async function generalCheck(type) {
        if (valueStartDate === dateSelected && valueEndDate === dateEndSelected &&
            valueCountry === countrySelected && valueCity === citySelected && valuePrize === selectedPrize &&
            valueFee === selectedFee && valueFormat === selectedFormat && valueType === selectedType &&
            valueLogoPath.path === selectedLogoPath.path && valueHeaderPath.path === selectedHeaderPath.path &&
            valueTrophyPath.path === selectedTrophyPath.path && valueMvpPath.path === selectedMvpPath.path &&
            valueName === selectedName && valueMaxTeams === selectedMaxTeams &&
            isChanged(selectedValueMap, valueMap) && isChanged(setedPrizePlaces, setedValuePrizePlaces) &&
            isChanged(setedPrizes, setedValuePrizes)) {
            type === "create" ? showNotification("Вы ничего не указали", "neutral") : showNotification("Вы ничего не изменили", "neutral")
            return false;
        } else if (dateSelected === "Выберите дату начала") {
            showNotification("Вы не указали дату начала турнира", "warn");
            return false;
        } else if (dateEndSelected === "Выберите дату окончания") {
            showNotification("Вы не указали дату окончания турнира", "warn");
            return false;
        } else if (!countrySelected.replace(/\s/g, '').length || countrySelected === "Выберите страну") {
            showNotification("Вы не указали страну", "warn");
            return false;
        } else if (isNotInList(props.countries, "countryRU", countrySelected)) {
            showNotification("Вы указали несуществующую страну", "warn");
            return false;
        } else if (citySelected !== "" && citySelected !== "Выберите город" && isNotInList(getItemFromDictByValue(props.countries, "countryRU", countrySelected).cities, "", citySelected)) {
            showNotification("Вы указали несуществующий город", "warn");
            return false;
        } else if (!selectedPrize.replace(/\s/g, '').length) {
            showNotification("Вы не указали приз", "warn");
            return false;
        } else if (!selectedFee.replace(/\s/g, '').length) {
            showNotification("Вы не указали взнос", "warn");
            return false;
        } else if (selectedFormat === "Выберите формат") {
            showNotification("Вы не указали формат", "warn");
            return false;
        } else if (selectedType === "Выберите тип") {
            showNotification("Вы не указали тип", "warn");
            return false;
        } else if (selectedLogoPath.path === "Укажите путь до логотипа") {
            showNotification("Вы не указали путь до логотипа", "warn");
            return false;
        } else if (selectedHeaderPath.path === "Укажите путь до шапки") {
            showNotification("Вы не указали путь до шапки", "warn");
            return false;
        } else if (selectedTrophyPath.path === "Укажите путь до трофея") {
            showNotification("Вы не указали путь до трофея", "warn");
            return false;
        } else if (selectedMvpPath.path === "Укажите путь до MVP") {
            showNotification("Вы не указали путь до MVP", "warn");
            return false;
        } else if (!selectedName.replace(/\s/g, '').length) {
            showNotification("Вы не указали название", "warn");
            return false;
        } else if (! /^[A-Za-z0-9! ]+$/.test(selectedName)) {
            showNotification("Название содержит недопустимые символы, либо русские буквы\n(Из специальных символов разрешён лишь !)", "warn");
            return false;
        } else if (!selectedMaxTeams.toString().replace(/\s/g, '').length) {
            showNotification("Вы не указали максимальное число команд", "warn");
            return false;
        } else if (isEmpty(selectedValueMap)) {
            showNotification("Вы не указали карты в пуле", "warn");
            return false;
        } else if (setedPrizePlaces === null || setedPrizePlaces.length !== parseInt(selectedMaxTeams)) {
            showNotification("Количество призовых мест не равно количеству участников", "warn");
            return false;
        } else if (isWrongPrizePlacesOrder(setedPrizePlaces)) {
            showNotification("Вы указали неправильный порядок призовых мест", "warn");
            return false;
        } else if (isDuplicatedPlacesExists(setedPrizePlaces)) {
            showNotification("Вы указали призовые места через тире, но их количество некорректно", "warn");
            return false;
        } else if (isEmpty(setedPrizes)) {
            showNotification("Вы не указали призы", "warn");
            return false;
        }
        return true;
    }


    async function sortEvents(headerDate, eventToSet) {
        let featuredEvents = [...props.featured];

        if (featuredEvents.filter(day => day.date === headerDate).length > 0) {
            featuredEvents.map((day) => {
                if (day.date === headerDate) {
                    day.events.push(eventToSet);
                    day.events.sort((a, b) => getDateFromEventDate(a.date).getTime() - getDateFromEventDate(b.date).getTime());
                }
            })
        } else {
            featuredEvents.push({ date: headerDate, events: [eventToSet] });
            featuredEvents.sort((a, b) => getDateByHeader(a.date).getTime() - getDateByHeader(b.date).getTime());
        }

        props.setFeaturedEvents(featuredEvents);
    }


    async function onEventCreated() {
        if (generalCheck("create")) {
            let prizePlaces_ = setedPrizePlaces.map((pPlace, index) => {
                return { place: pPlace, reward: setedPrizes[index] }
            })

            let eventDate = getEventDate(dateSelected, dateEndSelected);
            let headerDate = getHeaderDate(eventDate);

            let createdEvent = {
                headerSrc: URL.createObjectURL(selectedHeaderPath.file),
                headerFile: selectedHeaderPath.path,
                event: selectedName,
                eventSrc: URL.createObjectURL(selectedLogoPath.file),
                eventFile: selectedLogoPath.path,
                trophyFile: selectedTrophyPath.path,
                mvpFile: selectedMvpPath.path,
                flagPath: findKeyByName(props.countries, countrySelected, "flagPathMini", "countryRU"),
                country: countrySelected,
                city: citySelected !== "" && citySelected !== "Выберите город" ? citySelected : "",
                date: eventDate,
                format: selectedFormat,
                type: selectedType,
                registred: 0,
                total: selectedMaxTeams,
                fee: selectedFee,
                prize: selectedPrize,
                description: selectedValueDecription,
                prizePlaces: prizePlaces_,
                mapPool: selectedValueMap
            }

            try {
                await request("POST", "/createNewEvent", createdEvent);

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

                sortEvents(headerDate, createdEvent);

                setCreateEventActive(false);

                showNotification(`Турнир ${selectedName} успешно создан`, "ok");
            } catch (err) {
                showNotification(err.response.data.message, "warn")
            }
        }
    }


    async function onFeaturedEventEdited() {

        if (generalCheck("edit")) {
            let eventValueDate = getEventDate(valueStartDate, valueEndDate);
            let headerValueDate = getHeaderDate(eventValueDate);

            let day = getItemFromDictByValue(props.featured, "date", headerValueDate);
            let event = getItemFromDictByValue(day.events, "event", valueName);

            if (event.registred > parseInt(selectedMaxTeams)) {
                showNotification("Количество зарегистрированных участников больше максимального числа участников", "warn");
            } else {

                let prizePlaces_ = setedPrizePlaces.map((pPlace, index) => {
                    return { place: pPlace, reward: setedPrizes[index] }
                })

                let eventDate = getEventDate(dateSelected, dateEndSelected);
                let headerDate = getHeaderDate(eventDate);

                let editedEvent = {
                    headerSrc: valueHeaderPath.path !== selectedHeaderPath.path ? URL.createObjectURL(selectedHeaderPath.file) : event.headerSrc,
                    headerFile: selectedHeaderPath.path,
                    event: selectedName,
                    eventSrc: selectedLogoPath.path !== valueLogoPath.path ? URL.createObjectURL(selectedLogoPath.file) : event.eventSrc,
                    eventFile: selectedLogoPath.path,
                    trophyFile: selectedTrophyPath.path,
                    mvpFile: selectedMvpPath.path,
                    flagPath: findKeyByName(props.countries, countrySelected, "flagPathMini", "countryRU"),
                    country: countrySelected,
                    city: citySelected !== "" && citySelected !== "Выберите город" ? citySelected : "",
                    date: eventDate,
                    format: selectedFormat,
                    type: selectedType,
                    registred: event.registred,
                    total: selectedMaxTeams,
                    fee: selectedFee,
                    prize: selectedPrize,
                    description: selectedValueDecription,
                    prizePlaces: prizePlaces_,
                    mapPool: selectedValueMap
                }

                try {
                    await request("POST", `/editFeaturedEvent/${valueName}`, editedEvent);

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

                    let featuredEvents = [...props.featured];

                    featuredEvents.forEach(function (day, index, object) {
                        day.events = day.events.filter(event => event.event !== valueName);

                        if (day.events.length === 0) {
                            object.splice(index, 1);
                        }
                    });

                    sortEvents(headerDate, editedEvent);
                    setEventEditorActive(false);

                    showNotification(`Турнир ${valueName} успешно изменён`, "ok");
                } catch (err) {
                    showNotification(err.response.data.message, "warn")
                }
            }
        }

    }


    function getEventDate(start, end) {
        let dateStart = parseDateString(start, 0);
        let dateEnd = parseDateString(end, 0);
        if (dateStart.getFullYear() === dateEnd.getFullYear() && dateEnd.getFullYear() === new Date().getFullYear()) {
            return `${fixNumbers(dateStart.getDate())}.${fixNumbers(dateStart.getMonth() + 1)} - ${fixNumbers(dateEnd.getDate())}.${fixNumbers(dateEnd.getMonth() + 1)}`;
        }
        return `${fixNumbers(dateStart.getDate())}.${fixNumbers(dateStart.getMonth() + 1)}.${dateStart.getFullYear().toString().substring(2)} - ${fixNumbers(dateEnd.getDate())}.${fixNumbers(dateEnd.getMonth() + 1)}.${dateEnd.getFullYear().toString().substring(2)}`;
    }


    function getHeaderDate(date) {
        let splitedDate = date.split(" - ");
        let splitedStart = splitedDate[0].split(".");

        if (splitedStart.length === 3) {
            return `${getMonthName(parseInt(splitedStart[1]))} 20${splitedStart[2]}`;
        }
        return `${getMonthName(parseInt(splitedStart[1]))} ${new Date().getFullYear()}`;
    }


    function getDateFromEventDate(date) {
        let splitedDate = date.split(" - ");
        let splitedStart = splitedDate[0].split(".");
        if (splitedStart.length === 3) {
            return parseDateString(`${splitedStart[0]}.${splitedStart[1]}.20${splitedStart[2]}`);
        }
        return parseDateString(`${splitedStart[0]}.${splitedStart[1]}.${new Date().getFullYear()}`);
    }


    function getDateByHeader(date) {
        let splitedDate = date.split(" ");

        return new Date(parseInt(splitedDate[1]), getMonthIndexByName(splitedDate[0]), 1);
    }


    function getMonthIndexByName(month) {
        switch (month) {
            case "Январь":
                return 0;
            case "Февраль":
                return 1;
            case "Март":
                return 2;
            case "Апрель":
                return 3;
            case "Май":
                return 4;
            case "Июнь":
                return 5;
            case "Июль":
                return 6;
            case "Август":
                return 7;
            case "Сентябрь":
                return 8;
            case "Октябрь":
                return 9;
            case "Ноябрь":
                return 10;
            case "Декабрь":
                return 11;
        };
    }


    function isChanged(data1, data2) {
        if (data1 === null || data2 === null)
            return true;

        let result = data1.map((item, index) => {
            return item === data2[index];
        })

        return !result.includes(false);
    }


    return (
        <div>
            {props.ongoing && props.featured ?
                <div>
                    {props.ongoing !== null && props.ongoing.length > 0 || props.isAdmin ?
                        <div className="events_header">
                            <div className="row_center_5px">
                                <p>Текущие турниры</p>
                                {props.isAdmin ? <Editor size="15px" depth={1} onClick={() => { setChooseOngoingEventActive(true); setEventSelected("Выберите турнир"); setEventSelectorActive(false); }} /> : <></>}
                            </div>
                        </div>
                        :
                        <></>
                    }
                    <div className="events_spacer" style={{ alignItems: "flex-start" }}>
                        <div className="events_upcoming">
                            <OngoingEventsMaker events={props.ongoing} key={"current_events"} />
                        </div>
                    </div>

                    {props.featured !== null && props.featured.length > 0 || props.isAdmin ?
                        <div className="events_header">
                            <div className="row_center_5px">
                                <p>Будущие турниры</p>
                                {props.isAdmin ? <Editor size="15px" depth={1} onClick={() => setEventChooserActive(true)} /> : <></>}
                            </div>
                        </div>
                        :
                        <></>
                    }
                    <div className="events_spacer">
                        {props.featured !== null ? props.featured.map((month, i) =>
                            <div className="events_date_wrapper" key={i}>
                                <div className="events_date"><p>{month.date}</p></div>
                                <div className="events_upcoming">
                                    <OngoingEventsMaker events={month.events} />
                                </div>
                            </div>
                        ) : <></>}
                    </div>
                </div>
                :
                <Preloader />
            }

            {props.isAdmin ?
                <Login active={eventChooserActive} setActive={setEventChooserActive} key={"eventChooserActive"}>
                    <div className="header_splash_window">
                        <div className="logo_splash_window"></div>
                    </div>
                    <div className="info_text">
                        <p>Что Вы хотите сделать?</p>
                    </div>
                    <div className="buttons_wrapper">
                        <div className="dark_button">
                            <input type="submit" value="Изменить турнир" onClick={() => { setEventChooserActive(false); setChooseEventActive(true); setEventSelected("Выберите турнир"); setDateSelected("Выберите дату начала"); setDateSelectorActive(false); setEventSelectorActive(false) }} />
                        </div>
                        <div className="full_grey_button" style={{ margin: 0 }}>
                            <input type="submit" value="Создать турнир" onClick={() => { setEventChooserActive(false); setCreateEventActive(true); setCreateMatchInfo() }} />
                        </div>
                    </div>
                </Login>
                :
                <></>
            }

            {props.isAdmin ?
                <Login active={chooseEventActive} setActive={setChooseEventActive} key={"chooseEventActive"}>
                    <div className="header_splash_window">
                        <div className="logo_splash_window"></div>
                    </div>
                    <div className="info_text">
                        <p>Укажите информацию о турнире</p>
                    </div>
                    <div className="col_center_gap30">
                        <div className="inside">
                            <DefaultSelector issuer={"datePicker"} type={"datePicker"} styleMain={{ zIndex: 2 }} toggleClass={toggleDate} value={dateSelected} startValue={"Выберите дату начала"} setValue={setDateSelected} setEventSelected={setEventSelected} selectorActive={dateSelectorActive} minDate={new Date()} maxDate={genMaxDate()} />
                            {dateSelected !== "Выберите дату начала" ?
                                <SearchableSelector issuer={"ongoingEvents"} type={"full"} styleMain={null} styleItems={{ width: "464px" }} setSelectorActive={setEventSelectorActive} value={eventSelected} startValue={"Выберите турнир"} selectorActive={eventSelectorActive} data={getEventsByDate()} setValue={setEventSelected} itemKey={"event"} srcKey={"eventSrc"} />
                                :
                                <></>
                            }
                        </div>
                        {eventSelected !== "Выберите турнир" && !isNotInList(getEventsByDate(), "event", eventSelected) && props.eventMapPool !== null ?
                            <div className="full_grey_button">
                                <input type="submit" value="Подтвердить" onClick={() => { setEventEditorActive(true); setChooseEventActive(false); setOngoingMatchInfo(getItemFromDictByValue(getEventsByDate(), "event", eventSelected)) }} />
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
                <FullEventPopUp
                    active={eventEditorActive}
                    setActive={setEventEditorActive}
                    toggleDate={toggleDate}
                    dateSelected={dateSelected}
                    setDateSelected={setDateSelected}
                    dateSelectorActive={dateSelectorActive}
                    valueStartDate={valueStartDate}
                    valueEndDate={valueEndDate}
                    minStartDate={parseDateString(valueStartDate)}
                    minEndDate={parseDateString(valueEndDate)}
                    maxDate={genMaxDate()}
                    toggleEndDate={toggleEndDate}
                    dateEndSelected={dateEndSelected}
                    setDateEndSelected={setDateEndSelected}
                    dateEndSelectorActive={dateEndSelectorActive}
                    setCountrySelectorActive={setCountrySelectorActive}
                    countrySelected={countrySelected}
                    valueCountry={valueCountry}
                    countrySelectorActive={countrySelectorActive}
                    countries={props.countries}
                    setCountrySelected={setCountrySelected}
                    setCitySelected={setCitySelected}
                    setCitySelectorActive={setCitySelectorActive}
                    citySelected={citySelected}
                    valueCity={valueCity}
                    citySelectorActive={citySelectorActive}
                    selectedPrize={selectedPrize}
                    setSelectedPrize={setSelectedPrize}
                    selectedFee={selectedFee}
                    setSelectedFee={setSelectedFee}
                    toggleFormat={toggleFormat}
                    selectedFormat={selectedFormat}
                    valueFormat={valueFormat}
                    setSelectedFormat={setSelectedFormat}
                    formatSelectorActive={formatSelectorActive}
                    toggleType={toggleType}
                    selectedType={selectedType}
                    valueType={valueType}
                    setSelectedType={setSelectedType}
                    typeSelectorActive={typeSelectorActive}
                    selectedValueDecription={selectedValueDecription}
                    setSelectedValueDecription={setSelectedValueDecription}
                    selectedLogoPath={selectedLogoPath}
                    valueLogoPath={valueLogoPath}
                    handleImageUploaded={handleImageUploaded}
                    setSelectedLogoPath={setSelectedLogoPath}
                    selectedHeaderPath={selectedHeaderPath}
                    valueHeaderPath={valueHeaderPath}
                    setSelectedHeaderPath={setSelectedHeaderPath}
                    selectedTrophyPath={selectedTrophyPath}
                    valueTrophyPath={valueTrophyPath}
                    setSelectedTrophyPath={setSelectedTrophyPath}
                    selectedMvpPath={selectedMvpPath}
                    valueMvpPath={valueMvpPath}
                    setSelectedMvpPath={setSelectedMvpPath}
                    selectedName={selectedName}
                    setSelectedName={setSelectedName}
                    selectedMaxTeams={selectedMaxTeams}
                    setSelectedMaxTeams={setSelectedMaxTeams}
                    valueMap={valueMap}
                    selectorMapActive={selectorMapActive}
                    mapsActive={mapsActive}
                    setedPrizePlaces={setedPrizePlaces}
                    setedPrizes={setedPrizes}
                    setSetedPrizePlaces={setSetedPrizePlaces}
                    setSetedPrizes={setSetedPrizes}
                    setValueSetedPrizePlaces={setValueSetedPrizePlaces}
                    setValueSetedPrizes={setValueSetedPrizes}
                    setedValuePrizePlaces={setedValuePrizePlaces}
                    setedValuePrizes={setedValuePrizes}
                    selectedValueMap={selectedValueMap}
                    setValueMap={setValueMap}
                    setSelectedValueMap={setSelectedValueMap}
                    setSelectorMapActive={setSelectorMapActive}
                    setMapsActive={setMapsActive}
                    eventMapPool={props.eventMapPool}
                    toggleMap={toggleMap}
                    setDeleteEventActive={setDeleteEventActive}
                    valuePrize={valuePrize}
                    valueFee={valueFee}
                    valueDecription={valueDecription}
                    valueName={valueName}
                    valueMaxTeams={valueMaxTeams}
                    type={"edit"}
                    onFeaturedEventEdited={onFeaturedEventEdited}
                    key={"FullEventPopUp1"}
                />
                :
                <></>
            }

            {props.isAdmin ?
                <Login active={deleteEventActive} setActive={setDeleteEventActive} key={"deleteEventActive"}>
                    <div className="header_splash_window">
                        <div className="logo_splash_window"></div>
                    </div>
                    <div className="info_text">
                        <p>Вы уверены, что хотите удалить турнир {valueName}?</p>
                    </div>
                    <div className="small_buttons_wrapper">
                        <div className="small_dark_button">
                            <input type="submit" value="Нет" onClick={() => { setDeleteEventActive(false) }} />
                        </div>
                        <div className="small_grey_button">
                            <input type="submit" value="Да" onClick={() => { onEventDelete() }} />
                        </div>
                    </div>
                </Login>
                :
                <></>
            }

            {props.isAdmin ?
                <FullEventPopUp
                    active={createEventActive}
                    setActive={setCreateEventActive}
                    toggleDate={toggleDate}
                    dateSelected={dateSelected}
                    setDateSelected={setDateSelected}
                    dateSelectorActive={dateSelectorActive}
                    valueStartDate={"Выберите дату начала"}
                    valueEndDate={"Выберите дату окончания"}
                    minStartDate={new Date()}
                    minEndDate={new Date()}
                    maxDate={genMaxDate()}
                    toggleEndDate={toggleEndDate}
                    dateEndSelected={dateEndSelected}
                    setDateEndSelected={setDateEndSelected}
                    dateEndSelectorActive={dateEndSelectorActive}
                    setCountrySelectorActive={setCountrySelectorActive}
                    countrySelected={countrySelected}
                    valueCountry={valueCountry}
                    countrySelectorActive={countrySelectorActive}
                    countries={props.countries}
                    setCountrySelected={setCountrySelected}
                    setCitySelected={setCitySelected}
                    setCitySelectorActive={setCitySelectorActive}
                    citySelected={citySelected}
                    valueCity={valueCity}
                    citySelectorActive={citySelectorActive}
                    selectedPrize={selectedPrize}
                    setSelectedPrize={setSelectedPrize}
                    selectedFee={selectedFee}
                    setSelectedFee={setSelectedFee}
                    toggleFormat={toggleFormat}
                    selectedFormat={selectedFormat}
                    valueFormat={valueFormat}
                    setSelectedFormat={setSelectedFormat}
                    formatSelectorActive={formatSelectorActive}
                    toggleType={toggleType}
                    selectedType={selectedType}
                    valueType={valueType}
                    setSelectedType={setSelectedType}
                    typeSelectorActive={typeSelectorActive}
                    selectedValueDecription={selectedValueDecription}
                    setSelectedValueDecription={setSelectedValueDecription}
                    selectedLogoPath={selectedLogoPath}
                    valueLogoPath={valueLogoPath}
                    handleImageUploaded={handleImageUploaded}
                    setSelectedLogoPath={setSelectedLogoPath}
                    selectedHeaderPath={selectedHeaderPath}
                    valueHeaderPath={valueHeaderPath}
                    setSelectedHeaderPath={setSelectedHeaderPath}
                    selectedTrophyPath={selectedTrophyPath}
                    valueTrophyPath={valueTrophyPath}
                    setSelectedTrophyPath={setSelectedTrophyPath}
                    selectedMvpPath={selectedMvpPath}
                    valueMvpPath={valueMvpPath}
                    setSelectedMvpPath={setSelectedMvpPath}
                    selectedName={selectedName}
                    setSelectedName={setSelectedName}
                    selectedMaxTeams={selectedMaxTeams}
                    setSelectedMaxTeams={setSelectedMaxTeams}
                    valueMap={valueMap}
                    selectorMapActive={selectorMapActive}
                    mapsActive={mapsActive}
                    setedPrizePlaces={setedPrizePlaces}
                    setedPrizes={setedPrizes}
                    setSetedPrizePlaces={setSetedPrizePlaces}
                    setSetedPrizes={setSetedPrizes}
                    setValueSetedPrizePlaces={setValueSetedPrizePlaces}
                    setValueSetedPrizes={setValueSetedPrizes}
                    setedValuePrizePlaces={setedValuePrizePlaces}
                    setedValuePrizes={setedValuePrizes}
                    selectedValueMap={selectedValueMap}
                    setValueMap={setValueMap}
                    setSelectedValueMap={setSelectedValueMap}
                    setSelectorMapActive={setSelectorMapActive}
                    setMapsActive={setMapsActive}
                    eventMapPool={props.eventMapPool}
                    toggleMap={toggleMap}
                    valuePrize={""}
                    valueFee={""}
                    valueDecription={""}
                    valueName={""}
                    valueMaxTeams={""}
                    type={"create"}
                    onEventCreated={onEventCreated}
                    key={"FullEventPopUp2"}
                />
                :
                <></>
            }

            {props.isAdmin ?
                <Login active={chooseOngoingEventActive} setActive={setChooseOngoingEventActive} key={"chooseOngoingEventActive"}>
                    <div className="header_splash_window">
                        <div className="logo_splash_window"></div>
                    </div>
                    <div className="info_text">
                        <p>Укажите информацию о турнире</p>
                    </div>
                    <div className="col_center_gap30">
                        <SearchableSelector issuer={"ongoingEvents"} type={"full"} styleMain={{ zIndex: 2 }} styleItems={{ width: "464px" }} setSelectorActive={setEventSelectorActive} value={eventSelected} startValue={"Выберите турнир"} selectorActive={eventSelectorActive} data={props.ongoing} setValue={setEventSelected} itemKey={"event"} srcKey={"eventSrc"} />
                        {eventSelected !== "Выберите турнир" && getItemFromDictByValue(props.ongoing, "event", eventSelected) !== undefined ?
                            <div className="full_grey_button">
                                <input type="submit" value="Подтвердить" onClick={() => { setEditOngoingEventActive(true); setChooseOngoingEventActive(false); setMatchInfo() }} />
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
                <Login active={editOngoingEventActive} setActive={setEditOngoingEventActive} key={"editOngoingEventActive"}>
                    <div className="header_splash_window">
                        <div className="logo_splash_window"></div>
                    </div>
                    <div className="info_text">
                        <p>Укажите информацию о турнире</p>
                    </div>
                    <div className="col_center_gap30">
                        <div className="inside scroll" style={{ gap: "30px" }}>
                            <div className="col_center_gap10">
                                <div className="row_center_6">
                                    <div className="text-field_half">
                                        <input className="text-field_half input" style={{ color: selectedName === valueName ? "var(--white70)" : "white" }} type="text" name="prize" value={selectedName} placeholder="Укажите название" onChange={e => { setSelectedName(e.target.value) }} />
                                    </div>
                                    <div className="text-field_half">
                                        <input className="text-field_half input" style={{ color: selectedPrize === valuePrize ? "var(--white70)" : "white" }} type="text" name="prize" value={selectedPrize} placeholder="Укажите приз" onChange={e => { setSelectedPrize(e.target.value) }} />
                                    </div>
                                </div>
                                <div className="row_center_6">
                                    <div className="text-field_half">
                                        <input className="text-field_half input" style={{ color: selectedFee === valueFee ? "var(--white70)" : "white" }} type="text" name="prize" value={selectedFee} placeholder="Укажите взнос" onChange={e => { setSelectedFee(e.target.value) }} />
                                    </div>
                                    <FileLoader value={selectedLogoPath.path} startValue={valueLogoPath.path} handleImageUploaded={handleImageUploaded} setSelectedFile={setSelectedLogoPath} />
                                </div>
                                <div className="row_center_6">
                                    <FileLoader value={selectedHeaderPath.path} startValue={valueHeaderPath.path} handleImageUploaded={handleImageUploaded} setSelectedFile={setSelectedHeaderPath} />
                                    <FileLoader value={selectedTrophyPath.path} startValue={valueTrophyPath.path} handleImageUploaded={handleImageUploaded} setSelectedFile={setSelectedTrophyPath} />
                                </div>
                                <div className="row_center_6 display-on-start">
                                    <FileLoader value={selectedMvpPath.path} startValue={valueMvpPath.path} handleImageUploaded={handleImageUploaded} setSelectedFile={setSelectedMvpPath} />
                                </div>
                                <div className="text-field">
                                    <input className="text-field_input" type="text" style={{ color: selectedDiskLink === valueDiskLink ? "var(--white70)" : "white" }} name="diskLink" placeholder="Ссылка на диск с фото" value={selectedDiskLink} onChange={e => { setSelectedDiskLink(e.target.value) }} />
                                </div>
                            </div>
                            <div className="col_center_gap10">
                                <div className="info_text" style={{ padding: "0px" }}>
                                    <p>Призовые места</p>
                                </div>
                                {valueName !== "" && getItemFromDictByValue(props.ongoing, "event", valueName) !== undefined && selectorActive !== null && valueTeam !== null && teamsActive !== null && teams !== null ? generatePrizePlacesSelectors() : <></>}
                            </div>
                        </div>
                        <div className="col_center_gap_20">
                            <div className="full_grey_button">
                                <input type="submit" value="Подтвердить" onClick={() => { onEventEdited() }} />
                            </div>
                            <div className="leave_tournament_button display-row-center" style={{ margin: 0, width: "198px", height: "38px", padding: 0 }} onClick={() => { setEditOngoingEventActive(false); setDeleteEventActive(true) }}>
                                <p style={{ margin: 0, fontSize: "14px" }}>Удалить турнир</p>
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

export default OngoingEvents;