import React from "react";
import SearchableArraySelector from "../Selector/SearchableArraySelector";
import "../../pages/Match/Match.css";
import { showNotification } from "../../Utils/Utils";


function MapPoolGenerator(props) {
    const valueMap = props.valueMap;
    const selectorMapActive = props.selectorMapActive;
    const mapsActive = props.mapsActive;
    const eventMapPool = props.eventMapPool;
    const toggleMap = props.toggleMap;
    const selectedValueMap = props.selectedValueMap;
    const setSelectedValueMap = props.setSelectedValueMap;
    const setMapsActive = props.setMapsActive;
    const setValueMap = props.setValueMap;
    const setSelectorMapActive = props.setSelectorMapActive;


    function setMapValue(id, value) {
        let tempMaps = [...selectedValueMap];
        tempMaps[id] = value;
        setSelectedValueMap(tempMaps);
    }


    function setMap(id, value) { // с её помощью делаем нужную карту заблокированной
        let temp = [...mapsActive];
        let val = eventMapPool.indexOf(value);

        temp[val] = !temp[val];
        temp[id] = !temp[id];
        setMapsActive(temp);
    }


    function generateMapPoolSelectors() {
        let size = Math.ceil(valueMap.length / 2);

        let content = [];

        for (let index = 0; index < size; index++) {
            let subContent = [];

            // проверка на то, один ли элемент на данной строке
            const subIndex = (content.length + 1) === size && valueMap.length % 2 === 1 ? 1 : 2;
            for (let i = 0; i < subIndex; i++) {
                const idx = 2 * index + i;
                let startValue = valueMap[idx] === "" ? `Выберите карту ${(idx + 1)}` : valueMap[idx];
                subContent.push(
                    <div className="row_center_gap3" key={`mapPoolMapPair${i}/${index}`}>
                        {i === 0 ? <div className="minus" onClick={() => { deleteMapFromPool(idx) }}></div> : <></>}
                        <SearchableArraySelector issuer={"mapPool"} styleMain={null} toggleClass={toggleMap} value={selectedValueMap[idx]} startValue={startValue} selectorActive={selectorMapActive[idx]} data={eventMapPool} arrayActive={mapsActive} setDataValue={setMapValue} setItem={setMap} index={idx} type={"half"} />
                        {i === 1 ? <div className="minus" onClick={() => { deleteMapFromPool(idx) }}></div> : <></>}
                    </div>
                );
            }

            // проверка на то, один ли элемент на данной строке
            const style = subIndex === 1 ? { zIndex: (size - index), width: "506px" } : { zIndex: (size - index) };
            content.push(
                <div className="row_center_6" style={style} key={`mapPair${index}`}>
                    {subContent}
                </div>
            );
        }

        return content;
    }


    function deleteMapFromPool(index) {
        if (valueMap.length === 1) {
            showNotification("В пуле не может быть меньше 1 карты", "warn");
        } else {
            let tempMapsActive = [...mapsActive];
            let idx = eventMapPool.indexOf(valueMap[index]);

            if (idx !== -1) {
                tempMapsActive[idx] = false;
                setMapsActive(tempMapsActive);
            }

            let valueMaps = [...valueMap];
            valueMaps.splice(index, 1);

            let selectedMaps = [...selectedValueMap];
            selectedMaps.splice(index, 1);

            setValueMap(valueMaps);
            setSelectedValueMap(selectedMaps);

            let selectors = [...selectorMapActive];
            selectors.splice(index, 1);

            setSelectorMapActive(selectors);
        }
    }


    function addMapToPool() {
        if (valueMap !== null && selectedValueMap !== null) {
            let valueMaps = [...valueMap];
            let selectedMaps = [...selectedValueMap];

            valueMaps.push("");
            selectedMaps.push("");

            setValueMap(valueMaps);
            setSelectedValueMap(selectedMaps);

            let selectors = [...selectorMapActive];
            selectors.push(false);

            setSelectorMapActive(selectors);
        } else {
            setValueMap([""]);
            setSelectedValueMap([""]);
            setSelectorMapActive([false]);
            setMapsActive(Array(eventMapPool.length).fill(false));
        }
    }


    return (
        <div className="col_center_gap10">
            {valueMap !== null && selectorMapActive !== null && mapsActive !== null && eventMapPool !== null ?
                <div className="mapPoolItems">
                    {generateMapPoolSelectors()}
                </div>
                : <></>}
            {eventMapPool !== null ?
                <div className="add_stream" onClick={() => { addMapToPool() }}>
                    <p>Добавить карту в пул</p>
                    <img src="../../img/Add.svg" alt="Плюс" />
                </div>
                :
                <></>
            }
        </div>
    );
}

export default MapPoolGenerator;