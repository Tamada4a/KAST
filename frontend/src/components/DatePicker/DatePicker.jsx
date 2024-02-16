import React from "react";
import { useState, useEffect } from "react";
import "./DatePicker.css";

function DatePicker(props) {
    const monthNames = ["Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль",
        "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"];

    const dayNames = ["Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"];

    const [curMonth, setCurMonth] = useState(null);
    const [curYear, setCurYear] = useState(null);

    const minDate = props.minDate;
    const maxDate = props.maxDate;

    const [selectedDate, setSelectedDate] = useState(null);


    useEffect(() => {
        setCurMonth(new Date().getMonth());
    }, [new Date().getMonth()]);


    useEffect(() => {
        setCurYear(new Date().getFullYear());
    }, [new Date().getFullYear()]);


    function getNumberOfDaysInMonth(year, month) {
        return new Date(year, month + 1, 0).getDate();
    }


    function getSortedDays(year, month) {
        const dayIndex = new Date(year, month, 0).getDay();
        const firstHalf = dayNames.slice(dayIndex);
        return [...firstHalf, ...dayNames.slice(0, dayIndex)];
    }


    function range(start, end) {
        const len = Math.abs((end - start) / 1);
        const { result } = Array.from({ length: len }).reduce(
            ({ result, current }) => ({
                result: [...result, current],
                current: current + 1,
            }),
            { result: [], current: start }
        );

        return result;
    }


    function nextMonth() {
        if (curMonth < 11) {
            setCurMonth(prev => prev + 1);
        } else {
            setCurMonth(0);
            setCurYear(prev => prev + 1);
        }
    }


    function nextYear() {
        setCurYear(prev => prev + 1);
    }


    function prevMonth() {
        if (curMonth > 0) {
            setCurMonth(prev => prev - 1);
        } else {
            setCurMonth(11);
            setCurYear(prev => prev - 1);
        }
    }


    function prevYear() {
        setCurYear(prev => prev - 1);
    }


    function selectionHandler(event) {
        if (event.target.id === "day") {

            let dataDay = parseInt(event.target.getAttribute("data-day"));
            let date = new Date(curYear, curMonth, dataDay);
            setSelectedDate(date);

            if (checkMinMaxDate(dataDay, date)) {
                setDate(date);
            }
        }
    }


    function getTimeFromState(year, day, month = curMonth) {
        return new Date(year, month, day).getTime();
    }


    function checkMinMaxDate(day, date = selectedDate) {
        return date?.getTime() === getTimeFromState(curYear, day) && minDate <= getTimeFromState(curYear, day + 1) && maxDate >= getTimeFromState(curYear, day + 1);
    }


    function setDate(choosedDate) {
        let day = choosedDate.getDate();
        day = day < 10 ? `0${day}` : day;

        let month = choosedDate.getMonth() + 1;
        month = month < 10 ? `0${month}` : month;

        const date = `${day}.${month}.${choosedDate.getFullYear()}`;

        props.setDate(date);

        if (props.setValueTeam !== undefined) {
            props.setValueTeam("Выберите матч");
        }

        if (props.setEventSelected !== undefined) {
            props.setEventSelected("Выберите турнир");
        }

        if (props.setEndDate !== undefined) {
            props.setEndDate("Выберите дату окончания");
        }
    }


    return (
        <div>
            {curMonth !== null && curYear !== null ?
                <div className="picker_wrapper">
                    <div className="picker_header">
                        <button onClick={prevYear} disabled={minDate ? minDate > getTimeFromState(curYear - 1, 31) : null}>
                            <img src="../../img/leftArrow.svg" alt="prev year" />
                        </button>
                        <button onClick={prevMonth} disabled={minDate ? minDate > getTimeFromState(curYear, 1) : null}>
                            <img src="../../img/leftArrow.svg" alt="prev month" />
                        </button>

                        <p key={`${monthNames[curMonth]} ${curYear}`}>{monthNames[curMonth]} {curYear}</p>
                        <button onClick={nextMonth} disabled={maxDate ? maxDate < getTimeFromState(curYear, 31) : null}>
                            <img src="../../img/rightArrow.svg" alt="next month" />
                        </button>
                        <button onClick={nextYear} disabled={maxDate ? maxDate < getTimeFromState(curYear + 1, 1) : null}>
                            <img src="../../img/rightArrow.svg" alt="next year" />
                        </button>
                    </div>
                    <div className="picker_body">
                        <div className="seven_col_grid">
                            {getSortedDays(curYear, curMonth).map((day) =>
                                <span key={day}>{day}</span>
                            )}
                        </div>
                        <div className="seven_col_grid" onClick={(ev) => { selectionHandler(ev) }}>
                            {range(1, getNumberOfDaysInMonth(curYear, curMonth) + 1).map((day) =>
                                <p id="day" data-day={day} className={checkMinMaxDate(day) ? "date_selected" : ""} key={day}>
                                    {day}
                                </p>
                            )}
                        </div>
                    </div>
                </div>
                :
                <></>
            }
        </div>
    );
}

export default DatePicker;