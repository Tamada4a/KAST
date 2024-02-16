import React, { useState } from "react";
import "../Login/text_fields.css"
import { indexOf } from "../../Utils/Utils";

// Это селектор для моментов, где у меня индексы есть. Селекторы с блоком тоже сюда, возможно
function SearchableArraySelector(props) {

    const [data, setData] = useState(null);

    function filterFunction(subStr) {
        const results = props.data.filter(item => {
            if (subStr === "") return props.data
            if (props.itemKey) return item[props.itemKey].toLowerCase().startsWith(subStr.toLowerCase())
            return item.toLowerCase().startsWith(subStr.toLowerCase())
        })
        setData(results);
    }

    function getDrawable(item) {
        switch (props.issuer) {
            case "prizePlacesNonRegistration":
                return (
                    <li key={item[props.itemKey]} className={props.arrayActive[indexOf(item[props.itemKey], props.data, props.itemKey)] ? "text_field_half_options non_selectable" : "text_field_half_options"} onClick={() => { props.setDataValue(props.index, item[props.itemKey]); props.setItem(indexOf(item[props.itemKey], props.data, props.itemKey), props.value); props.toggleClass(props.index) }}>
                        <div className="list_logo">
                            <img src={item[props.srcKey]} alt={item[props.itemKey]} />
                        </div>
                        <p>{item[props.itemKey]}</p>
                    </li>
                );
            case "mapPool":
                return (
                    <li key={item} className={props.arrayActive[props.data.indexOf(item)] ? "text_field_half_options non_selectable" : "text_field_half_options"} onClick={() => { props.setDataValue(props.index, item); props.setItem(props.data.indexOf(item), props.value); props.toggleClass(props.index) }}>
                        <p>{item}</p>
                    </li>
                );
            case "eventTeamSelector":
                return (
                    <li key={item[props.itemKey]} className={props.arrayActive[indexOf(item[props.itemKey], props.data, props.itemKey)] ? "text_field_half_options non_selectable" : "text_field_half_options"} onClick={() => { props.setDataValue(item[props.itemKey]); props.setItem(indexOf(item[props.itemKey], props.data, props.itemKey), props.value); props.toggleClass() }}>
                        <div className="list_logo">
                            <img src={item[props.srcKey]} alt={item[props.itemKey]} />
                        </div>
                        <p>{item[props.itemKey]}</p>
                    </li>
                );
            case "eventPlayerSelector":
                return (
                    <li key={item[props.itemKey]} className={props.arrayActive[indexOf(item[props.itemKey], props.data, props.itemKey)] ? "text_field_half_options non_selectable" : "text_field_half_options"} onClick={() => { props.setDataValue(props.index, item[props.itemKey]); props.setItem(indexOf(item[props.itemKey], props.data, props.itemKey), props.value); props.toggleClass(props.index) }}>
                        <div className="list_logo">
                            <img src={item[props.srcKey]} alt={item[props.itemKey]} />
                        </div>
                        <p>{item[props.itemKey]}</p>
                    </li>
                );
            case "country":
                return (
                    <li key={item[props.itemKey]} className="text_field_third_options" onClick={() => { props.setDataValue(props.index, item[props.itemKey], item[props.srcKey]); props.toggleClass(props.index) }}>
                        <img src={item[props.srcKey]} alt={item[props.itemKey]} />
                        <p>{item[props.itemKey]}</p>
                    </li>
                );
        }
    }


    function setDataValue(val) {
        if (props.index !== undefined)
            props.setDataValue(props.index, val);
        else
            props.setDataValue(val);
    }


    function setItem(val) {
        if (props.issuer !== "country") {
            if (props.itemKey !== undefined) {
                props.data.map((item) => {
                    props.setItem(indexOf(val, props.data, props.itemKey), props.value);
                });
            } else {
                props.setItem(props.data.indexOf(val), props.value);
            }
        }
    }


    function toggleClass() {
        if (props.index !== undefined)
            props.toggleClass(props.index, data !== null && data.length !== 0);
        else
            props.toggleClass(data !== null && data.length !== 0);
    }


    function getMainClassName() {
        if (props.type === "half")
            return "text-field_half";
        else
            return "text-field_third";
    }


    function getSecondClassName() {
        if (props.type === "half")
            return "text-field_half_selector";
        else
            return "text-field_third_selector";
    }


    function getThirdClassName() {
        if (props.type === "half")
            return "text-field_half_img_wrapper";
        else
            return "text-field_third_img_wrapper";
    }


    function getListType() {
        if (props.selectorActive) {
            if (props.type === "half")
                return "select_list";
            else
                return "select_list_third";
        } else {
            if (props.type === "half")
                return "select_list hide";
            else
                return "select_list_third hide";
        }
    }


    function setInputValue() {
        if (props.value === props.startValue || props.value === null || props.value === undefined)
            return "";
        return props.value;
    }


    return (
        <div className={getMainClassName()} style={props.styleMain}>
            <div className={getSecondClassName()}>
                <div className={getThirdClassName()}>
                    <input type="text" placeholder={props.startValue} value={setInputValue()} onChange={e => { setDataValue(e.target.value); setItem(e.target.value); filterFunction(e.target.value); toggleClass() }} />
                    <img src="../../img/arrow.svg" id="arrowIcon" className={props.selectorActive ? 'rotate' : null} alt="arrow" onClick={() => props.toggleClass(props.index)} />
                </div>
                <ul className={getListType()}>
                    {data !== null ?
                        data.map((item) =>
                            getDrawable(item)
                        )
                        :
                        props.data !== null ?
                            props.data.map((item) =>
                                getDrawable(item)
                            )
                            :
                            <></>
                    }
                </ul>
            </div>
        </div>
    )
}

export default SearchableArraySelector;