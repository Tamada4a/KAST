import React from "react";
import "../Login/text_fields.css"
import { indexOf } from "../../Utils/Utils";

// Это селектор для моментов, где у меня индексы есть, но не нужен поиск
function DefaultArraySelector(props) {

    function getDrawable(item) {
        switch (props.issuer) {
            case "picks&Bans":
                return (
                    <li key={item[props.itemKey]} className={props.arrayActive[indexOf(item[props.itemKey], props.data, props.itemKey)] ? "text_field_half_options non_selectable" : "text_field_half_options"} onClick={() => { props.setDataValue(props.index, item[props.itemKey]); props.setItem(indexOf(item[props.itemKey], props.data, props.itemKey), props.value); props.toggleClass(props.index) }}>
                        <p>{item[props.itemKey]}</p>
                    </li>
                );
        }
    }


    return (
        <div className="text-field_half" style={props.styleMain}>
            <div className="text-field_half_selector">
                <div className="text_field_half_select" onClick={() => props.toggleClass(props.index)}>
                    <p className={props.value === props.startValue ? "onStart" : "choosed"}>{props.value}</p>
                    <img src="../../img/arrow.svg" className={props.selectorActive ? 'rotate' : null} alt="arrow" />
                </div>
                <ul className={props.selectorActive ? 'select_list' : 'select_list hide'}>
                    {props.data !== null ?
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

export default DefaultArraySelector;