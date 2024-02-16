import React from "react";
import { useState, useEffect } from "react";
import Editor from "../../../components/Editor/Editor";
import Login from "../../../components/Login/Login";
import "./Description.css"
import SearchableArraySelector from "../../../components/Selector/SearchableArraySelector";
import DefaultArraySelector from "../../../components/Selector/DefaultArraySelector";
import DefaultSelector from "../../../components/Selector/DefaultSelector";
import { isNotInList, indexOf, showNotification } from "../../../Utils/Utils";
import { request } from "../../../Utils/MyAxios";

function Description(props) {

    const [infoEditorActive, setInfoEditorActive] = useState(false); //состояния модального окна для редактирования текущих матчей

    const [selectedFormat, setSelectedFormat] = useState(`bo${props.match.format[8]}`); // формат (bo1 и тп)
    const [formatSelectorActive, setFormatSelectorActive] = useState(false);

    const [valueDescription, setDescription] = useState(""); // описание

    const [mapsActive, setMapsActive] = useState(null); // состояния команд - выбрана ли команда(чтоб блокировать ее)
    const [pickActive, setPickActive] = useState(null);

    const [selectorMapActive, setSelectorMapActive] = useState(null); // состояния селектора
    const [selectorPickActive, setSelectorPickActive] = useState(null); // состояния селектора

    const [valuePick, setValuePick] = useState(null); //Для селектора команды
    const [valueMap, setValueMap] = useState(null); //Для селектора команды

    const [pickState, setPickState] = useState(null);


    useEffect(() => {
        setDescription(props.match.description);

        startSet(parseInt(selectedFormat[2]));
    }, []);


    function getTeamName(team) {
        if (team === undefined || team === null)
            return "";
        let start = props.match.partType === "team" ? "Команда " : "Игрок ";
        return (start + team);
    }


    function getActionName(action) {
        switch (action) {
            case "pick":
                return "выбирает";
            case "ban":
                return "убирает";
            case "decider":
                return "Остаётся";
        }
    }


    function startSet(size) {
        setPickState([
            { name: `${props.match.firstTeam.name} банит`, count: 0 },
            { name: `${props.match.firstTeam.name} пикает`, count: 0 },
            { name: `${props.match.secondTeam.name} банит`, count: 0 },
            { name: `${props.match.secondTeam.name} пикает`, count: 0 },
            { name: "Десайдер", count: 0 },
        ]);

        setValueMap(Array(props.match.mapPool.length).fill().map((_, index) => `Выберите карту ${(index + 1)}`));

        setValuePick(Array(props.match.mapPool.length).fill("Выберите пик"));

        setSelectorPickActive(Array(props.match.mapPool.length).fill(false));
        setSelectorMapActive(Array(props.match.mapPool.length).fill(false));

        setMapsActive(Array(props.match.mapPool.length).fill(false));

        let tempPickActive = Array(5).fill(false);

        if (size === 1) {
            tempPickActive[1] = true;
            tempPickActive[3] = true;
        } else if (size === 2) {
            tempPickActive[4] = true;
        }

        if (size === props.match.mapPool.length) {
            tempPickActive[0] = true;
            tempPickActive[2] = true;
        }

        setPickActive(tempPickActive);
    }


    function setFormat(value) {
        startSet(parseInt(value[2]));

        setSelectedFormat(value)
    }


    function setPick(id, value) {
        let tempPick = [...pickState];
        tempPick[id].count = tempPick[id].count + 1;

        const bo = parseInt(selectedFormat[2]);

        let temp = [...pickActive];


        let bansFirst = Math.ceil((props.match.mapPool.length - bo) / 2);
        let bansSecond = props.match.mapPool.length - bo - bansFirst;

        if (id === 0 && tempPick[id].name.includes("банит") && tempPick[id].count >= bansFirst) {
            temp[id] = true;
        } else if (id === 2 && tempPick[id].name.includes("банит") && tempPick[id].count >= bansSecond) {
            temp[id] = true;
        }

        // всё, связанное с idx, отвечает за раблокировку нужных полей в случае замены действия в данном селекторе
        let idx = indexOf(value, pickState, "name");
        if (idx !== undefined) {
            tempPick[idx].count = tempPick[idx].count - 1;
        }

        if (idx === 0 && tempPick[idx].name.includes("банит") && tempPick[idx].count < bansFirst) {
            temp[idx] = false;
        } else if (idx === 2 && tempPick[idx].name.includes("банит") && tempPick[idx].count < bansSecond) {
            temp[idx] = false;
        }

        if (bo === 1) {
            if (tempPick[id].name.includes("Десайдер") && tempPick[id].count >= 1) {
                temp[id] = true;
            }

            if (idx !== undefined && tempPick[idx].name.includes("Десайдер") && tempPick[idx].count < 1) {
                temp[idx] = false;
            }
        } else if (bo === 2) {
            if (tempPick[id].name.includes("пикает") && tempPick[id].count >= 1) {
                temp[id] = true;
            }

            if (idx !== undefined && tempPick[idx].name.includes("пикает") && tempPick[idx].count < 1) {
                temp[idx] = false;
            }

        } else if (bo === 3) {
            if (tempPick[id].name.includes("пикает") && tempPick[id].count >= 1) {
                temp[id] = true;
            } else if (tempPick[id].name.includes("Десайдер") && tempPick[id].count >= 1) {
                temp[id] = true;
            }

            if (idx !== undefined && tempPick[idx].name.includes("пикает") && tempPick[idx].count < 1) {
                temp[idx] = false;
            } else if (idx !== undefined && tempPick[idx].name.includes("Десайдер") && tempPick[idx].count < 1) {
                temp[idx] = false;
            }
        } else if (bo === 5) {
            if (tempPick[id].name.includes("пикает") && tempPick[id].count >= 2) {
                temp[id] = true;
            } else if (tempPick[id].name.includes("Десайдер") && tempPick[id].count >= 1) {
                temp[id] = true;
            }

            if (idx !== undefined && tempPick[idx].name.includes("пикает") && tempPick[idx].count < 2) {
                temp[idx] = false;
            } else if (idx !== undefined && tempPick[idx].name.includes("Десайдер") && tempPick[idx].count < 1) {
                temp[idx] = false;
            }
        }
        setPickActive(temp);
        setPickState(tempPick);
    }


    function setPickValue(id, name) {
        let tempPicks = [...valuePick];
        tempPicks[id] = name;
        setValuePick(tempPicks);
    }


    function setMapValue(id, value) {
        let tempMaps = [...valueMap];
        tempMaps[id] = value;
        setValueMap(tempMaps);
    }


    function setMap(id, value) { // с её помощью делаем нужную карту заблокированной
        let temp = [...mapsActive];
        let val = props.match.mapPool.indexOf(value);

        temp[val] = !temp[val];
        temp[id] = !temp[id];
        setMapsActive(temp);
    }


    function togglePick(id) { // функция toggle для селектора
        let temp = [];
        for (let i = 0; i < selectorPickActive.length; ++i) {
            temp.push(false);
        }
        if (id !== "all") {
            temp[id] = !selectorPickActive[id];
        }
        setSelectorPickActive(temp);
    };


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


    function selectorGenerator() {
        let content = [];
        let size = props.match.mapPool.length;
        for (let i = 0; i < size; ++i) {
            content.push(
                <div className="row_center_6" key={`picks&Bans/${i}`}>
                    <SearchableArraySelector issuer={"mapPool"} styleMain={{ zIndex: size - i }} toggleClass={toggleMap} value={valueMap[i]} startValue={`Выберите карту ${(i + 1)}`} selectorActive={selectorMapActive[i]} data={props.match.mapPool} arrayActive={mapsActive} setDataValue={setMapValue} setItem={setMap} index={i} type={"half"} />
                    <DefaultArraySelector issuer={"picks&Bans"} styleMain={{ zIndex: size - i }} toggleClass={togglePick} value={valuePick[i]} startValue={"Выберите пик"} selectorActive={selectorPickActive[i]} data={pickState} arrayActive={pickActive} setDataValue={setPickValue} setItem={setPick} index={i} itemKey={"name"} />
                </div>
            );
        }
        return content;
    }


    function toggleFormat() {
        setFormatSelectorActive(!formatSelectorActive);
    }


    async function onInfoEdited() {
        let isMapsChanged = valueMap.some((map, index) => map !== (`Выберите карту ${(index + 1)}`));
        let isPickChanged = valuePick.some(pick => pick !== "Выберите пик");

        let atLeastOneMapMissed = mapsActive.some(mapActive => !mapActive);
        let atLeastOnePickMissed = valuePick.some(pick => pick === "Выберите пик");

        if (selectedFormat === (`bo${props.match.format[8]}`) && valueDescription === props.match.description && !isMapsChanged && !isPickChanged) {
            showNotification("Вы ничего не изменили", "neutral");
        } else if (isPickChanged && atLeastOneMapMissed) {
            showNotification("Вы не указали все карты", "warn");
        } else if (isMapsChanged && atLeastOnePickMissed) {
            showNotification("Вы не указали все действия", "warn");
        } else if (valueMap.some(map => isNotInList(props.match.mapPool, "", map)) && isMapsChanged) {
            showNotification("Вы указали несуществующую карту", "warn");
        } else if (valueMap.some(map => valueMap.filter(m => m == map).length > 1)) {
            showNotification("Вы указали несколько одинаковых карт", "warn");
        } else {
            let editedMatch = {
                ...props.match,
                picks: props.match.picks === null ? getMatchPicks() : props.match.picks,
                format: (`Best of ${selectedFormat[2]}`),
                description: valueDescription,
                maps: getMatchMaps(props.match.maps, isMapsChanged)
            }
            try {
                await request("POST", `editMatchDesc/${props.matchId}`, editedMatch);

                props.setMatch(editedMatch);

                showNotification("Вы успешно изменили информацию", "ok");
                setInfoEditorActive(false);
            } catch (err) {
                showNotification(err.response.data.message, "warn")
            }
        }
    }


    function getMatchPicks() {
        if (valuePick.includes("Выберите пик")) {
            return null;
        }

        return valuePick.map((pick, index) => {
            if (pick.includes("Десайдер"))
                return { type: "decider", map: valueMap[index] };
            if (pick.includes("банит"))
                return { team: pick.replace(" банит", ""), type: "ban", map: valueMap[index] }
            return { team: pick.replace(" пикает", ""), type: "pick", map: valueMap[index] }
        })
    }


    function getMatchMaps(maps, isMapsChanged) {
        if (maps.some(map => map.mapName === "TBA") && isMapsChanged) {
            return valueMap.map((map) => {
                return { mapName: map, status: "upcoming", scoreFirst: null, scoreSecond: null, firstHalf: null, secondHalf: null, overtime: null, stats: null }
            })
        }
        return maps;
    }


    function resetMapsEditor() {
        setFormatSelectorActive(false);
        toggleMap("all");
        togglePick("all");
        setSelectedFormat(`bo${props.match.format[8]}`);
        setValueMap(props.match.mapPool.map((map, index) => {
            return (`Выберите карту ${(index + 1)}`);
        }))

        setValuePick(Array(props.match.mapPool.length).fill("Выберите пик"));
        setDescription(props.match.description);
    }


    return (
        <div>
            <div className="row_center_5px" style={{ marginBottom: "5px" }}>
                <p className="p_fixer">Карты</p>
                {props.isAdmin && props.match.matchStatus !== 2 ? <Editor size="14px" depth={2} onClick={() => { resetMapsEditor(); setInfoEditorActive(true) }} /> : <></>}
            </div>
            <div className="match_info_upcoming_maps_desc">
                {
                    props.match.description !== "" ?
                        <p>{props.match.format} ({props.match.type})<br /><br />* {props.match.description}</p>
                        :
                        <p>{props.match.format} ({props.match.type})</p>
                }
            </div>
            {props.match.matchStatus === 0 || props.match.picks === null ? null :
                <div className="maps_news">
                    {props.match.picks.map((pick, index) => {
                        return <p key={index}>{`${(index + 1)}. ${getTeamName(pick.team)} ${getActionName(pick.type)} ${pick.map}`}</p>
                    })}
                </div>
            }

            {props.isAdmin ?
                <Login active={infoEditorActive} setActive={setInfoEditorActive}>
                    <div className="header_splash_window">
                        <div className="logo_splash_window"></div>
                    </div>
                    <div className="info_text">
                        <p>Укажите информацию о картах</p>
                    </div>
                    <div className="col_center_gap30">
                        <div className="inside scroll">
                            <div className="row_center_6" style={{ alignItems: "flex-start", width: "100%" }}>
                                <div className="text-field_half">
                                    <DefaultSelector issuer={"default"} type={"half"} styleMain={{ zIndex: (props.match.mapPool.length + 1) }} styleItems={null} toggleClass={toggleFormat} value={selectedFormat} startValue={`bo${props.match.format[8]}`} setValue={setFormat} selectorActive={formatSelectorActive} data={["bo1", "bo2", "bo3", "bo5", "bo7"]} />
                                </div>
                            </div>
                            <div className="description_field">
                                <textarea type="text" placeholder="Введите описание" style={{ width: "434px", color: valueDescription === props.match.description ? "var(--white70)" : "white", fontSize: "16px", height: "65px" }} onChange={ev => { setDescription(ev.target.value) }} value={valueDescription} />
                            </div>
                            {props.match.picks === null && mapsActive !== null && pickActive !== null && pickState !== null && valueMap !== null && valuePick !== null && selectorMapActive !== null && selectorPickActive !== null ? selectorGenerator() : <></>}
                        </div>
                        <div className="full_grey_button">
                            <input type="submit" value="Подтвердить" onClick={() => { onInfoEdited() }} />
                        </div>
                    </div>
                </Login>
                :
                <></>
            }
        </div>
    )
}

export default Description;