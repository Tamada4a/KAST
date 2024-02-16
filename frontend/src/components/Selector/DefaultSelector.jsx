import React from "react";
import DatePicker from "../DatePicker/DatePicker";
import TimePicker from "../TimePicker/TimePicker";
import "../Login/text_fields.css"
import { setTier } from "../../Utils/Utils";

// Это обычный селектор с выпадающим списком - без поля ввода
function DefaultSelector(props) {
    function getDrawable() {
        switch (props.issuer) {
            case "datePicker":
                return (
                    <li className="date_options" key="datePickerElem">
                        <DatePicker setEndDate={props.setEndDate} setEventSelected={props.setEventSelected} setValueTeam={props.setValueTeam} setDate={props.setValue} minDate={props.minDate} maxDate={props.maxDate} key={"datePicker"}/>
                    </li>
                );
            case "timePicker":
                return (
                    <TimePicker setTime={props.setValue} minTime={props.startValue === "Выберите время" ? "00:00" : props.startValue} key={"timePicker"}/>
                );
            case "default":
                return (
                    props.data.map((item) =>
                        <li key={item} className="text_field_half_options" onClick={() => { props.setValue(item); props.toggleClass() }}>
                            <p>{item}</p>
                        </li>
                    )
                );
            case "tier":
                let content = [];
                for (let i = 0; i < 5; ++i) {
                    const value = (i + 1) === 1 ? "1 звезда" : (i + 1) < 5 ? (`${(i + 1)} звезды`) : "5 звёзд";
                    content.push(
                        <li key={value} className="text_field_half_options" style={{ height: "49px" }} onClick={() => { props.setValue(value); props.toggleClass() }}>
                            {props.depth ? setTier(i + 1, props.depth) : setTier(i + 1)}
                        </li>
                    )
                }
                return content;
        }
    }


    function getListType() {
        if (props.selectorActive) {
            if (props.type === "half" && props.issuer !== "timePicker" && props.issuer !== "datePicker")
                return "select_list";
            else if (props.type === "full" && props.issuer !== "timePicker" && props.issuer !== "datePicker")
                return "select_list_full";
            else if (props.issuer === "timePicker")
                return "time_list";
            return "select_date";
        } else {
            if (props.type === "half" && props.issuer !== "timePicker" && props.issuer !== "datePicker")
                return "select_list hide";
            else if (props.type === "full" && props.issuer !== "timePicker" && props.issuer !== "datePicker")
                return "select_list_full hide";
            else if (props.issuer === "timePicker")
                return "time_list hide";
            return "select_date hide";
        }
    }


    function getPClassName() {
        if (props.issuer === "timePicker") {
            return props.value === "Выберите время" ||  props.value === props.startValue ? "onStart" : "choosed"; 
        }
        return props.value === props.startValue ? "onStart" : "choosed";
    }


    return (
        <div className={props.type === "half" ? "text-field_half_selector" : "text-field_selector"} style={props.styleMain}>
            <div className={props.type === "half" ? "text_field_half_select" : "text_field_select"} onClick={props.toggleClass}>
                <p className={getPClassName()}>{props.value}</p>
                <img src="../../img/arrow.svg" className={props.selectorActive ? 'rotate' : null} alt="arrow" />
            </div>
            <ul className={getListType()} style={props.styleItems}>
                {getDrawable()}
            </ul>
        </div>
    )
}

export default DefaultSelector;