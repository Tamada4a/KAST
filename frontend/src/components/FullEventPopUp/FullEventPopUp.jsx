import React from "react";
import "../EventMaker/EventInfo/EventInfo.css"
import Login from "../Login/Login";
import DefaultSelector from "../Selector/DefaultSelector";
import SearchableSelector from "../Selector/SearchableSelector";
import FileLoader from "../FileLoader/FileLoader";
import PrizePlaceGenerator from "../PrizePlace/PrizePlaceGenerator";
import MapPoolGenerator from "../Map/MapPoolGenerator";
import { getItemFromDictByValue, parseDateString, isInt, showNotification } from "../../Utils/Utils";

function FullEventPopUp(props) {
    const active = props.active;
    const setActive = props.setActive;
    const toggleDate = props.toggleDate;
    const dateSelected = props.dateSelected;
    const setDateSelected = props.setDateSelected;
    const dateSelectorActive = props.dateSelectorActive;
    const valueStartDate = props.valueStartDate;
    const valueEndDate = props.valueEndDate;
    const minStartDate = props.minStartDate;
    const minEndDate = props.minEndDate;
    const maxDate = props.maxDate;
    const toggleEndDate = props.toggleEndDate;
    const dateEndSelected = props.dateEndSelected;
    const setDateEndSelected = props.setDateEndSelected;
    const dateEndSelectorActive = props.dateEndSelectorActive;
    const setCountrySelectorActive = props.setCountrySelectorActive;
    const countrySelected = props.countrySelected;
    const valueCountry = props.valueCountry;
    const countrySelectorActive = props.countrySelectorActive;
    const countries = props.countries;
    const setCountrySelected = props.setCountrySelected;
    const setCitySelected = props.setCitySelected;
    const setCitySelectorActive = props.setCitySelectorActive;
    const citySelected = props.citySelected;
    const valueCity = props.valueCity;
    const citySelectorActive = props.citySelectorActive;
    const selectedPrize = props.selectedPrize;
    const selectedFee = props.selectedFee;
    const setSelectedPrize = props.setSelectedPrize;
    const setSelectedFee = props.setSelectedFee;
    const toggleFormat = props.toggleFormat;
    const selectedFormat = props.selectedFormat;
    const valueFormat = props.valueFormat;
    const setSelectedFormat = props.setSelectedFormat;
    const formatSelectorActive = props.formatSelectorActive;
    const toggleType = props.toggleType;
    const selectedType = props.selectedType;
    const valueType = props.valueType;
    const setSelectedType = props.setSelectedType;
    const typeSelectorActive = props.typeSelectorActive;
    const selectedValueDecription = props.selectedValueDecription;
    const setSelectedValueDecription = props.setSelectedValueDecription;
    const selectedLogoPath = props.selectedLogoPath;
    const valueLogoPath = props.valueLogoPath;
    const handleImageUploaded = props.handleImageUploaded;
    const setSelectedLogoPath = props.setSelectedLogoPath;
    const selectedHeaderPath = props.selectedHeaderPath;
    const valueHeaderPath = props.valueHeaderPath;
    const setSelectedHeaderPath = props.setSelectedHeaderPath;
    const selectedTrophyPath = props.selectedTrophyPath;
    const valueTrophyPath = props.valueTrophyPath;
    const setSelectedTrophyPath = props.setSelectedTrophyPath;
    const selectedMvpPath = props.selectedMvpPath;
    const valueMvpPath = props.valueMvpPath;
    const setSelectedMvpPath = props.setSelectedMvpPath;
    const selectedName = props.selectedName;
    const setSelectedName = props.setSelectedName;
    const selectedMaxTeams = props.selectedMaxTeams;
    const setSelectedMaxTeams = props.setSelectedMaxTeams;
    const valueMap = props.valueMap;
    const selectorMapActive = props.selectorMapActive;
    const mapsActive = props.mapsActive;
    const setedPrizePlaces = props.setedPrizePlaces;
    const setedPrizes = props.setedPrizes;
    const setSetedPrizePlaces = props.setSetedPrizePlaces;
    const setSetedPrizes = props.setSetedPrizes;
    const setValueSetedPrizePlaces = props.setValueSetedPrizePlaces;
    const setValueSetedPrizes = props.setValueSetedPrizes;
    const setedValuePrizePlaces = props.setedValuePrizePlaces;
    const setedValuePrizes = props.setedValuePrizes;
    const selectedValueMap = props.selectedValueMap;
    const setValueMap = props.setValueMap;
    const setSelectedValueMap = props.setSelectedValueMap;
    const setSelectorMapActive = props.setSelectorMapActive;
    const setMapsActive = props.setMapsActive;
    const eventMapPool = props.eventMapPool;
    const toggleMap = props.toggleMap;
    const valuePrize = props.valuePrize;
    const valueFee = props.valueFee;
    const valueDecription = props.valueDecription;
    const valueName = props.valueName;
    const valueMaxTeams = props.valueMaxTeams;


    function checkMaxTeams(str) {
        if (!isInt(str) && str !== "") {
            showNotification("Это поле может быть только числом", "warn");
        }
        else {
            setSelectedMaxTeams(str);
            if (str === "")
                str = 99999;

            if (setedPrizePlaces !== null && setedPrizePlaces.length > parseInt(str)) {
                let diff = setedPrizePlaces.length - parseInt(str);
                deletePrizePlace(setedPrizePlaces.length - diff, diff);
            } else if (setedPrizePlaces !== null && str !== 99999) {
                showNotification("Количество участников больше количества призовых мест. Добавьте призовые места", "neutral");
            }
        }
    }


    function deletePrizePlace(index, count = 1) {
        let tempPrizePlaces = [...setedPrizePlaces];
        let tempPrizes = [...setedPrizes];

        tempPrizePlaces.splice(index, count);
        tempPrizes.splice(index, count);

        setSetedPrizePlaces(tempPrizePlaces);
        setSetedPrizes(tempPrizes);

        let valuePrizePlaces = [...setedValuePrizePlaces];
        let valuePrizes = [...setedValuePrizes]

        valuePrizePlaces.splice(index, count);
        valuePrizes.splice(index, count);

        setValueSetedPrizePlaces(valuePrizePlaces);
        setValueSetedPrizes(valuePrizes);
    }


    return (
        <Login active={active} setActive={setActive}>
            <div className="header_splash_window">
                <div className="logo_splash_window"></div>
            </div>
            <div className="info_text">
                <p>Укажите информацию о турнире</p>
            </div>
            <div className="col_center_gap30">
                <div className="inside scroll" style={{ gap: "30px" }}>
                    <div className="col_center_gap10">
                        <DefaultSelector issuer={"datePicker"} type={"datePicker"} styleMain={{ zIndex: 4 }} toggleClass={toggleDate} value={dateSelected} startValue={valueStartDate} setValue={setDateSelected} selectorActive={dateSelectorActive} minDate={minStartDate} maxDate={maxDate} setEndDate={setDateEndSelected} />
                        <DefaultSelector issuer={"datePicker"} type={"datePicker"} styleMain={{ zIndex: 3 }} toggleClass={toggleEndDate} value={dateEndSelected} startValue={valueEndDate} setValue={setDateEndSelected} selectorActive={dateEndSelectorActive} minDate={dateSelected !== "Выберите дату начала" ? parseDateString(dateSelected) : minEndDate} maxDate={maxDate} />
                        <div className="row_center_6 display-on-start" style={{ zIndex: 2 }}>
                            <SearchableSelector issuer={"country"} type={"half"} styleMain={null} styleItems={null} setSelectorActive={setCountrySelectorActive} value={countrySelected} startValue={valueCountry} selectorActive={countrySelectorActive} data={countries} setValue={setCountrySelected} setCity={setCitySelected} itemKey={"countryRU"} srcKey={"flagPathMini"} />
                            {countrySelected !== "Выберите страну" && getItemFromDictByValue(countries, "countryRU", countrySelected) !== undefined ?
                                <SearchableSelector issuer={"city"} type={"half"} styleMain={null} styleItems={null} setSelectorActive={setCitySelectorActive} value={citySelected} startValue={valueCity} selectorActive={citySelectorActive} data={getItemFromDictByValue(countries, "countryRU", countrySelected).cities} setValue={setCitySelected} />
                                :
                                <></>
                            }
                        </div>
                        <div className="row_center_6">
                            <div className="text-field_half">
                                <input className="text-field_half input" type="text" name="prize" style={{ color: selectedPrize === valuePrize ? "var(--white70)" : "white" }} value={selectedPrize} placeholder="Укажите приз" onChange={e => { setSelectedPrize(e.target.value) }} />
                            </div>
                            <div className="text-field_half">
                                <input className="text-field_half input" type="text" name="prize" style={{ color: selectedFee === valueFee ? "var(--white70)" : "white" }} value={selectedFee} placeholder="Укажите взнос" onChange={e => { setSelectedFee(e.target.value) }} />
                            </div>
                        </div>
                        <div className="row_center_6" style={{ zIndex: 1 }}>
                            <div className="text-field_half">
                                <DefaultSelector issuer={"default"} type={"half"} styleMain={null} styleItems={null} toggleClass={toggleFormat} value={selectedFormat} startValue={valueFormat} setValue={setSelectedFormat} selectorActive={formatSelectorActive} data={["1x1", "2x2", "5x5"]} />
                            </div>
                            <div className="text-field_half">
                                <DefaultSelector issuer={"default"} type={"half"} styleMain={null} styleItems={null} toggleClass={toggleType} value={selectedType} startValue={valueType} setValue={setSelectedType} selectorActive={typeSelectorActive} data={["Lan", "Online"]} />
                            </div>
                        </div>
                        <div className="description_field">
                            <textarea type="text" placeholder="Введите формат игр/описание" style={{ width: "434px", fontSize: "16px", height: "65px", color: selectedValueDecription === valueDecription ? "var(--white70)" : "white" }} onChange={e => { setSelectedValueDecription(e.target.value) }} value={selectedValueDecription} />
                        </div>
                        <div className="row_center_6">
                            <FileLoader value={selectedLogoPath.path} startValue={valueLogoPath.path} handleImageUploaded={handleImageUploaded} setSelectedFile={setSelectedLogoPath} />
                            <FileLoader value={selectedHeaderPath.path} startValue={valueHeaderPath.path} handleImageUploaded={handleImageUploaded} setSelectedFile={setSelectedHeaderPath} />
                        </div>
                        <div className="row_center_6">
                            <FileLoader value={selectedTrophyPath.path} startValue={valueTrophyPath.path} handleImageUploaded={handleImageUploaded} setSelectedFile={setSelectedTrophyPath} />
                            <FileLoader value={selectedMvpPath.path} startValue={valueMvpPath.path} handleImageUploaded={handleImageUploaded} setSelectedFile={setSelectedMvpPath} />
                        </div>
                        <div className="row_center_6">
                            <div className="text-field_half">
                                <input className="text-field_half input" type="text" name="prize" style={{ color: selectedName === valueName ? "var(--white70)" : "white" }} value={selectedName} placeholder="Укажите название" onChange={e => { setSelectedName(e.target.value) }} />
                            </div>
                            <div className="text-field_half">
                                <input className="text-field_half input" type="text" name="prize" style={{ color: selectedMaxTeams === valueMaxTeams ? "var(--white70)" : "white" }} value={selectedMaxTeams} placeholder="Укажите макс. команд" onChange={e => { checkMaxTeams(e.target.value) }} />
                            </div>
                        </div>
                    </div>
                    <div className="col_center_gap10" style={(valueMap === null && selectorMapActive === null && mapsActive === null) || (valueMap.length === 0 && selectorMapActive.length === 0) ? { gap: "0px" } : null}>
                        <div className="info_text" style={{ padding: "0px" }}>
                            <p>Пул карт</p>
                        </div>
                        <MapPoolGenerator
                            valueMap={valueMap}
                            selectorMapActive={selectorMapActive}
                            mapsActive={mapsActive}
                            eventMapPool={eventMapPool}
                            toggleMap={toggleMap}
                            selectedValueMap={selectedValueMap}
                            setSelectedValueMap={setSelectedValueMap}
                            setMapsActive={setMapsActive}
                            setValueMap={setValueMap}
                            setSelectorMapActive={setSelectorMapActive}
                        />
                    </div>
                    <div className="col_center_gap10" style={(setedPrizePlaces === null && setedPrizes === null) || (setedPrizePlaces.length === 0 && setedPrizes.length === 0) ? { gap: "0px" } : null}>
                        <div className="info_text" style={{ padding: "0px" }}>
                            <p>Призовые места</p>
                        </div>
                        <PrizePlaceGenerator
                            setedPrizes={setedPrizes}
                            setSetedPrizes={setSetedPrizes}
                            setedPrizePlaces={setedPrizePlaces}
                            setSetedPrizePlaces={setSetedPrizePlaces}
                            setedValuePrizePlaces={setedValuePrizePlaces}
                            setValueSetedPrizePlaces={setValueSetedPrizePlaces}
                            setedValuePrizes={setedValuePrizes}
                            setValueSetedPrizes={setValueSetedPrizes}
                            selectedMaxTeams={selectedMaxTeams}
                        />
                    </div>
                </div>
                {
                    props.type === "create" ?
                        <div className="col_center_gap_20">
                            <div className="join_tournament_button display-row-center" style={{ margin: 0, width: "198px", height: "38px", padding: 0 }} onClick={() => { props.onEventCreated() }}>
                                <p style={{ margin: 0, fontSize: "14px" }}>Создать турнир</p>
                            </div>
                        </div>
                        :
                        <div className="col_center_gap_20">
                            <div className="full_grey_button">
                                <input type="submit" value="Подтвердить" onClick={() => { props.onFeaturedEventEdited() }} />
                            </div>
                            <div className="leave_tournament_button display-row-center" style={{ margin: 0, width: "198px", height: "38px", padding: 0 }} onClick={() => { setActive(false); props.setDeleteEventActive(true) }}>
                                <p style={{ margin: 0, fontSize: "14px" }}>Удалить турнир</p>
                            </div>
                        </div>
                }
            </div>
        </Login>
    )
}

export default FullEventPopUp;