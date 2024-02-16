import React from "react";
import { showNotification } from "../../Utils/Utils";

function PrizePlaceGenerator(props) {
    function checkPrizePlaces(index, str) {
        let splitedStr = str.split("-");
        if (str.includes("-") && checkAllIsInt(splitedStr) && (splitedStr.length - 1) === 1) {
            setPrizePlaces(index, str);
        } else if (!isInt(str) && str !== "") {
           showNotification("Это поле может быть только числом", "warn");
        } else if (parseInt(str) === 0) {
           showNotification("Призовое место является числом больше нуля", "warn");
        } else if (parseInt(str) !== 1 && str !== "" && index === 0) {
           showNotification("Первое призовое место должно начинаться с единицы", "warn");
        } else if (isInt(str) || str === "") {
            setPrizePlaces(index, str);
        }
    }


    function checkAllIsInt(str) {
        for (let i = 0; i < str.length; ++i) {
            if (!isInt(str[i]) && str[i].replace(/\s/g, '').length)
                return false;
        }
        return true;
    }


    function isInt(str) {
        return !isNaN(str) && !isNaN(parseInt(str))
    }


    function setPrizes(id, value) {
        let tempPrizes = [...props.setedPrizes];
        tempPrizes[id] = value;
        props.setSetedPrizes(tempPrizes);
    }


    function setPrizePlaces(id, value) {
        let tempPrizePlaces = [...props.setedPrizePlaces];
        tempPrizePlaces[id] = value;
        props.setSetedPrizePlaces(tempPrizePlaces);
    }


    function deletePrizePlace(index, count = 1) {
        let tempPrizePlaces = [...props.setedPrizePlaces];
        let tempPrizes = [...props.setedPrizes];

        tempPrizePlaces.splice(index, count);
        tempPrizes.splice(index, count);

        props.setSetedPrizePlaces(tempPrizePlaces);
        props.setSetedPrizes(tempPrizes);

        let valuePrizePlaces = [...props.setedValuePrizePlaces];
        let valuePrizes = [...props.setedValuePrizes]

        valuePrizePlaces.splice(index, count);
        valuePrizes.splice(index, count);

        props.setValueSetedPrizePlaces(valuePrizePlaces);
        props.setValueSetedPrizes(valuePrizes);
    }


    function generatePrizePlaces() {
        let content = [];

        let size = props.setedPrizePlaces.length;

        for (let i = 0; i < size; ++i) {
            content.push(
                <div className="row_center_gap3" style={{ paddingLeft: "14px" }} key={`prizePlaceEditorGenerator${i}`}>
                    <div className="row_center_6">
                        <div className="text-field_half">
                            <input className="text-field_half input" type="text" placeholder="Укажите призовое место" style={{ color: props.setedPrizePlaces[i] === props.setedValuePrizePlaces[i] ? "var(--white70)" : "white" }} value={props.setedPrizePlaces[i]} onChange={e => { checkPrizePlaces(i, e.target.value) }} />
                        </div>
                        <div className="text-field_half">
                            <input className="text-field_half input" placeholder="Укажите приз" style={{ color: props.setedPrizes[i] === props.setedValuePrizes[i] ? "var(--white70)" : "white" }} value={props.setedPrizes[i]} onChange={e => { setPrizes(i, e.target.value) }} />
                        </div>
                    </div>
                    <div className="minus" onClick={() => { deletePrizePlace(i) }}></div>
                </div>
            );
        }

        return content;
    }


    function addPrizePlace() {
        if (!isInt(props.selectedMaxTeams)) {
           showNotification("Сначала необходимо установить количество участников", "warn");
        } else {
            if (props.setedPrizePlaces !== null && props.setedPrizes !== null) {
                if (props.setedPrizePlaces.length + 1 > parseInt(props.selectedMaxTeams)) {
                   showNotification("Количество призовых мест должно равняться количеству участников", "warn");
                } else if (!isPrevPlaceFinished()) {
                   showNotification("Предыдущее призовое место заполнено не полностью", "warn");
                } else {
                    let prizePlaces = [...props.setedPrizePlaces];
                    let prizes = [...props.setedPrizes]

                    prizePlaces.push("");
                    prizes.push("");

                    props.setSetedPrizePlaces(prizePlaces);
                    props.setSetedPrizes(prizes);

                    let valuePrizePlaces = [...props.setedValuePrizePlaces];
                    let valuePrizes = [...props.setedValuePrizes]

                    valuePrizePlaces.push("");
                    valuePrizes.push("");

                    props.setValueSetedPrizePlaces(valuePrizePlaces);
                    props.setValueSetedPrizes(valuePrizes);
                }
            } else if (parseInt(props.selectedMaxTeams) === 0) {
               showNotification("Количество призовых мест должно равняться количеству участников", "warn");
            } else {
                props.setSetedPrizePlaces([""]);
                props.setSetedPrizes([""]);

                props.setValueSetedPrizePlaces([""]);
                props.setValueSetedPrizes([""]);
            }
        }
    }


    function isPrevPlaceFinished() {
        if (props.setedPrizePlaces.length === 0)
            return true;

        let prizePlace = props.setedPrizePlaces[props.setedPrizePlaces.length - 1].split("-");
        if (prizePlace.length === 2) {
            return (prizePlace.length - 1) === 1 && isInt(prizePlace[0]) && isInt(prizePlace[1]) && parseInt(prizePlace[0]) < parseInt(prizePlace[1]);
        }
        return isInt(prizePlace[0]);
    }


    return (
        <div className="col_center_gap10">
            {props.setedPrizePlaces !== null && props.setedPrizes !== null ? generatePrizePlaces() : <></>}
            <div className="add_stream" onClick={() => { addPrizePlace() }}>
                <p>Добавить призовое место</p>
                <img src="../../img/Add.svg" alt="Плюс" />
            </div>
        </div>
    );
}

export default PrizePlaceGenerator;