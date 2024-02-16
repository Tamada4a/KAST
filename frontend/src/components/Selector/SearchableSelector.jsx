import React, { useState } from "react";
import "../Login/text_fields.css"
import "../../pages/Matches/Matches.css";
import { fixTagLength } from "../../Utils/Utils";

// Селектор с полем для ввода
function SearchableSelector(props) {
    const [data, setData] = useState(null);

    function filterFunction(subStr) {
        const results = props.data.filter(item => {
            if (subStr === "") return props.data
            if (props.type === "match") return item[props.itemKey].toLowerCase().startsWith(subStr.toLowerCase()) || item[props.itemSecondKey].toLowerCase().startsWith(subStr.toLowerCase())
            if (props.itemKey) return item[props.itemKey].toLowerCase().startsWith(subStr.toLowerCase())
            return item.toLowerCase().startsWith(subStr.toLowerCase())
        })
        setData(results);
    }


    function resetCity() {
        if (props.setCity !== undefined)
            props.setCity("Выберите город");
    }


    function resetValueTeam() {
        if (props.setValueTeam !== undefined) {
            props.setValueTeam("Выберите матч");
        }
    }


    function getDrawable(item) {
        switch (props.issuer) {
            case "country":
                return (
                    <li key={item[props.itemKey]} className='text_field_half_options' onClick={() => { props.setValue(item[props.itemKey]); props.setSelectorActive(false); resetCity() }} style={props.styleItems}>
                        <img src={item[props.srcKey]} alt={item[props.itemKey]} />
                        <p>{item[props.itemKey]}</p>
                    </li>
                );
            case "city":
                return (
                    <li key={item} className='text_field_half_options' onClick={() => { props.setValue(item); props.setSelectorActive(false) }} style={props.styleItems}>
                        <p>{item}</p>
                    </li>
                );
            case "ongoingEvents":
                return (
                    <li key={item[props.itemKey]} className='text_field_options' onClick={() => { props.setValue(item[props.itemKey]); props.setSelectorActive(false) }} style={props.styleItems}>
                        <div className="row_center_5px">
                            <div className="list_logo">
                                <img src={item[props.srcKey]} alt={item[props.itemKey]} />
                            </div>
                            <p>{item[props.itemKey]}</p>
                        </div>
                    </li>
                );
            case "eventSelector":
                return (
                    <li key={item[props.itemKey]} className='text_field_half_options' onClick={() => { props.setValue(item[props.itemKey]); props.setSelectorActive(false); resetValueTeam() }}>
                        <div className="list_logo">
                            <img src={item[props.srcKey]} alt={item[props.itemKey]} />
                        </div>
                        <p>{item[props.itemKey]}</p>
                    </li>
                );
            case "matches":
                return (
                    <li key={`${item[props.itemKey]} vs. ${item[props.itemSecondKey]}`} className="select_match_options" onClick={() => { props.setMatchId(item.matchId); props.setValue(`${item[props.itemKey]} vs. ${item[props.itemSecondKey]}`); props.setValueDate(item[props.dateKey]); props.setDateSelected(item[props.dateKey]); props.setTimeSelected(item[props.timeKey]); props.setValueTime(item[props.timeKey]); props.setEventSelected(item[props.eventKey]); props.setValueEvent(item[props.eventKey]); props.setItem(item[props.itemKey]); props.setSecondItem(item[props.itemSecondKey]); props.setSelectorActive(false) }}>
                        <div className="match_list_team_wrapper">
                            <div className="left_team_tag"><p>{fixTagLength(item[props.itemKey])}</p></div>
                            <div className="list_logo">
                                <img src={item.images[props.srcKey]} alt={item[props.itemKey]} />
                            </div>
                        </div>
                        <p> vs. </p>
                        <div className="match_list_team_wrapper">
                            <div className="list_logo">
                                <img src={item.images[props.srcSecondKey]} alt={item[props.itemSecondKey]} />
                            </div>
                            <div className="right_team_tag"><p>{fixTagLength(item[props.itemSecondKey])}</p></div>
                        </div>
                    </li>
                );
        }
    }


    function getMainClassName() {
        if (props.type === "half")
            return "text-field_half";
        else if (props.type === "full")
            return "text-field_selector";
        else if (props.type === "match")
            return "match_selector";
    }


    function getSecondClassName() {
        if (props.type === "half")
            return "text-field_half_selector";
        else if (props.type === "full")
            return "text-field_selector";
        else if (props.type === "match")
            return "match_selector";
    }


    function getThirdClassName() {
        if (props.type === "half")
            return "text-field_half_img_wrapper";
        else if (props.type === "full")
            return "text-field_img_wrapper";
        else if (props.type === "match")
            return "match_select";
    }


    function getListType() {
        if (props.selectorActive) {
            if (props.type === "half")
                return "select_list";
            else if (props.type === "full")
                return "select_list_full";
            else if (props.type === "match")
                return "match_select_list";
        } else {
            if (props.type === "half")
                return "select_list hide";
            else if (props.type === "full")
                return "select_list_full hide";
            else if (props.type === "match")
                return "match_select_list hide";
        }
    }


    return (
        <div className={getMainClassName()} style={props.styleMain}>
            <div className={getSecondClassName()}>
                <div className={getThirdClassName()}>
                    <input type="text" placeholder={props.startValue} value={props.value === props.startValue ? "" : props.value} onChange={e => { props.setValue(e.target.value); filterFunction(e.target.value); props.setSelectorActive(data && data.length !== 0) }} />
                    <img src="../../img/arrow.svg" id="arrowIcon" className={props.selectorActive ? 'rotate' : null} alt="arrow" onClick={() => props.setSelectorActive(!props.selectorActive)} />
                </div>
                <ul className={getListType()} style={props.styleItems}>
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

export default SearchableSelector;