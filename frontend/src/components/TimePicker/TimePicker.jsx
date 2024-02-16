import React from "react";
import { useState, useEffect } from "react";
import "./TimePicker.css";

function TimePicker(props) {
    const [selectedHour, setSelectedHour] = useState('');
    const [selectedMinute, setSelectedMinute] = useState('');

    const [selectorHourActive, setSelectorHourActive] = useState([false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]); // состояния селектора
    const [selectorMinuteActive, setSelectorMinuteActive] = useState([false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]); // состояния селектора


    const [minHour, setMinHour] = useState(null);
    const [minMinute, setMinMinute] = useState(null);


    useEffect(() => {
        if (props.minTime !== undefined && props.minTime !== "") {
            setMinHour(parseInt(props.minTime.split(":")[0]));
        }
    }, [parseInt(props.minTime.split(":")[0])]);


    useEffect(() => {
        if (props.minTime !== undefined && props.minTime !== "") {
            setMinMinute(parseInt(props.minTime.split(":")[1]));
        }
    }, [parseInt(props.minTime.split(":")[1])]);


    function handleData(event) {
        if (event.target.id === "hour") {
            const hour = event.target.getAttribute("data-hour");

            let temp = [...selectorHourActive];

            if (hour >= minHour) {

                if (selectedHour !== "") {
                    temp[parseInt(selectedHour)] = !temp[parseInt(selectedHour)];
                }
                temp[parseInt(hour)] = !temp[parseInt(hour)];

                setSelectorHourActive(temp);
                setSelectedHour(hour);

                if (selectedMinute === "") {
                    let minute = parseInt(minMinute) < 10 ? (`0${parseInt(minMinute)}`) : minMinute;
                    setMinMinute(minute);
                    props.setTime(`${hour}:${minute}`);
                } else {
                    props.setTime(`${hour}:${selectedMinute}`);
                }
            }

        } else if (event.target.id === "minute") {
            const minute = event.target.getAttribute("data-minute");

            let temp = [...selectorMinuteActive];

            if (minute >= minMinute) {

                if (selectedMinute !== "") {
                    temp[parseInt(selectedMinute)] = !temp[parseInt(selectedMinute)];
                }
                temp[parseInt(minute)] = !temp[parseInt(minute)];

                setSelectorMinuteActive(temp);
                setSelectedMinute(minute);

                if (selectedHour === "") {
                    let hour = parseInt(minHour) < 10 ? (`0${parseInt(minHour)}`) : minHour
                    setMinHour(hour);
                    props.setTime(`${hour}:${minute}`);
                } else {
                    props.setTime(`${selectedHour}:${minute}`);
                }
            }
        }
    }


    function isHoursChanged() {
        return selectedHour !== "" && parseInt(selectedHour) > minHour;
    }


    function hoursGenerator() {
        let hours = [];
        for (let i = 0; i < 24; ++i) {
            const hour = i < 10 ? (`0${i}`) : i;

            hours.push(
                <li key={`${hour} часов`} className={i >= minHour ? (selectorHourActive[i] ? "select_time_options time_selected" : "select_time_options") : "select_time_options non_selectable"} onClick={handleData}>
                    <p id="hour" data-hour={hour}>{hour}</p>
                </li>
            );
        }
        return hours;
    }


    const minutesGenerator = () => {
        let minutes = [];
        for (let i = 0; i < 60; ++i) {
            let min = i < 10 ? (`0${i}`) : i;

            let tempMinMinute = isHoursChanged() ? 0 : minMinute;

            minutes.push(
                <li key={`${min} минут`} className={i >= tempMinMinute ? (selectorMinuteActive[i] ? "select_time_options time_selected" : "select_time_options") : "select_time_options non_selectable"} onClick={handleData}>
                    <p id="minute" data-minute={min}>{min}</p>
                </li>
            );
        }
        return minutes;
    }


    return (
        <div className="display_inline">
            <ul className="select_time">
                {minHour !== null ? hoursGenerator() : <></>}
            </ul>
            <ul className="select_time">
                {minMinute !== null ? minutesGenerator() : <></>}
            </ul>
        </div>
    );
}

export default TimePicker;
