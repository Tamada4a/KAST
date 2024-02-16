import { request } from "./MyAxios";
import { toast } from "react-hot-toast";
import Cookies from 'universal-cookie';


const cookies = new Cookies(null, { path: '/' });


export function getStoredPlayerNick() {
    return cookies.get('playerNick');
};


export function setStoredPlayerNick(nick, expires = 1) {
    cookies.set('playerNick', nick, { expires: new Date(new Date().getTime() + expires * 3600000) });
};


export function fillSpaces(event) {
    if (event.includes(" "))
        return event.replaceAll(" ", "-");
    return event;
}


export function unFillSpaces(event) {
    if (event.includes("-"))
        return event.replaceAll("-", " ");
    return event;
}


export function fillDots(event) {
    if (event.includes("."))
        return event.replaceAll(".", "-");
    return event;
}


export function matchUrlMaker(id, left, right, event, date) {
    return `/match/${id}/${fillSpaces(left)}-${fillSpaces(right)}-${fillSpaces(event)}-${fillDots(date)}`;
}


export function indexOf(value, data, key) {
    for (let i = 0; i < data.length; ++i) {
        if (value === data[i][key]) {
            return i;
        }
    }
}


export async function onImageUploaded(file, path, name) {
    const image = new FormData();
    image.append('imageFile', file);
    image.append('imageName', file.name);
    image.append("path", path);
    image.append("name", name);
    await request("POST", "/uploadImage", image, { "Content-Type": "multi-part/formdata" });
}


export function placeType(place) {
    const first = "brightness(0) saturate(100%) invert(19%) sepia(71%) saturate(1218%) hue-rotate(25deg) brightness(99%) contrast(103%)";
    const second = "brightness(0) saturate(100%) invert(30%) sepia(0%) saturate(0%) hue-rotate(213deg) brightness(97%) contrast(86%)";
    const third = "invert(16%) sepia(86%) saturate(556%) hue-rotate(351deg) brightness(98%) contrast(100%)";

    if (place.startsWith("1")) {
        return (
            <div className="achievement" style={{ background: "#EDB404" }}>
                <img src="../../img/Trophy.svg" style={{ filter: first }} alt="gold" />
                <p style={{ color: "#503B00" }}>{getPrizePlaceEnding(place)}</p>
            </div>
        );
    }
    else if (place.startsWith("2")) {
        return (
            <div className="achievement" style={{ background: "#C0C0C0" }}>
                <img src="../../img/Trophy.svg" style={{ filter: second }} alt="silver" />
                <p style={{ color: "#525252" }}>{getPrizePlaceEnding(place)}</p>
            </div>
        );
    }
    else if (place.startsWith("3")) {
        return (
            <div className="achievement" style={{ background: "#CD7F32" }}>
                <img src="../../img/Trophy.svg" style={{ filter: third }} alt="bronze" />
                <p style={{ color: "#502A05" }}>{getPrizePlaceEnding(place)}</p>
            </div>
        );
    }
}


export function showNotification(desc, border) {
    const toastStyle = {
        padding: "10px 4px",
        width: "246px",
        height: "auto",
        minHeight: "28px",

        background: "var(--base-09)",

        color: "white",
        fontFamily: "var(--text-medium-lcg)",
        fontSize: "12px"
    };

    switch (border) {
        case "neutral":
            toastStyle.border = "1px solid white";
            break;
        case "ok":
            toastStyle.border = "1px solid var(--base-05)";
            break;
        case "warn":
            toastStyle.border = "1px solid var(--base-12)";
            break;
    }

    if (!desc.includes("Вас пригласили в команду"))
        toast(desc, { style: toastStyle });
    else {
        toastStyle.justifyContent = "center";
        toastStyle.display = "flex";
        toastStyle.flexDirection = "column";
        toastStyle.padding = "0px";
        toastStyle.margin = "-4px -10px";
        toastStyle.alignItems = "center";
        toastStyle.borderRadius = "8px";
        toastStyle.width = "254px";

        toast((t) => (
            <div style={toastStyle}>
                <p>{desc}</p>
                <div className="row_center_7" style={{ marginBottom: "7px" }}>
                    <div className="small_dark_button" >
                        <input type="submit" value="Нет" style={{ fontSize: "12px", fontFamily: "var(--text-medium-lcg)" }} onClick={e => { inviteDecision(desc.replace("Вас пригласили в команду ", ""), "no", t.id) }} />
                    </div>
                    <div className="small_dark_button" >
                        <input type="submit" value="Да" style={{ fontSize: "12px", fontFamily: "var(--text-medium-lcg)" }} onClick={e => { inviteDecision(desc.replace("Вас пригласили в команду ", ""), "yes", t.id) }} />
                    </div>
                </div>
            </div>
        ), { duration: 120000 });
    }
}


async function inviteDecision(teamName, decision, toastId) {
    if (getStoredPlayerNick() !== null && getStoredPlayerNick() !== "null" && getStoredPlayerNick() !== "undefined" && getStoredPlayerNick() !== undefined) {
        const requestURL = `/inviteDecision/${teamName}/${getStoredPlayerNick()}/${decision}`;
        await request("POST", requestURL, {});
        toast.dismiss(toastId);
    }
}


export function isNotInList(data, key, value) {
    if (data === null) {
        return true;
    }

    let result = true;
    if (key !== "") {
        result = getItemFromDictByValue(data, key, value) === undefined;
    } else {
        data.map((item) => {
            if (item === value)
                result = false;
        })
    }
    return result;
}


export function getItemFromDictByValue(data, key, value) {
    for (let i = 0; i < data.length; ++i) {
        if (data[i][key] === value) {
            return data[i];
        }
    }
}


export async function getMatchesWithImg(matches) {
    return await Promise.all(
        matches.map(async match => ({
            ...match,
            images: await getMatchImages(match)
        }))
    );
}


export async function getMatchImages(match) {
    const [leftTeamSrc, rightTeamSrc, eventSrc] = await Promise.all([
        getImage(match.leftTeam),
        getImage(match.rightTeam),
        getImage(match.event)
    ]);
    return { leftTeamSrc, rightTeamSrc, eventSrc };
}


export async function getImage(id, type = "/other") {
    const response = await request("GET", `/getImage/${id}${type}`, {}, { "Content-Type": "image/jpeg" }, "blob");
    return URL.createObjectURL(response.data);
}


export function parseDateString(strDate, day = 1) {
    let splitedDate = strDate.split(".");
    return new Date(splitedDate[2], parseInt(splitedDate[1]) - 1, parseInt(splitedDate[0]) + day);
}


export function getTimeOfString(strTime, strDate = "") {
    let split = strTime.split(":");
    let date = strDate === "" ? new Date() : parseDateString(strDate);
    date.setHours(parseInt(split[0]));
    date.setMinutes(parseInt(split[1]));

    return date.getTime();
}


export function genMaxDate() {
    const date = new Date();
    date.setFullYear(date.getFullYear() + 10000);

    return date;
}


export function setTier(tier, dirLevel = "") {
    let content = [];
    for (let i = 0; i < 5; ++i) {
        if (i < tier) {
            content.push(
                <img src={`${dirLevel}img/Top_star.svg`} alt="star" key={`star${i}`} />
            );
        }
        else {
            content.push(
                <img src={`${dirLevel}img/Top_star.svg`} style={{ opacity: 0.3 }} alt="faded_star" key={`faded_star${i}`} />
            );
        }
    }
    return content;
}


export function fixNumbers(number) {
    return number < 10 ? (`0${number}`) : number;
}


export function findKeyByName(data, value, key, nameKey) {
    return data.filter(item => item[nameKey] === value)[0][key];
}


export function isCorrectTime(minTime, selectedTime) {
    let splitedStart = minTime.split(":");
    let splitedSelected = selectedTime.split(":");

    return !(parseInt(splitedStart[0]) > parseInt(splitedSelected[0]) || (parseInt(splitedStart[0]) === parseInt(splitedSelected[0]) && parseInt(splitedSelected[1]) < parseInt(splitedStart[1])));
}


export function getMonthName(month) {
    switch (month) {
        case 1:
            return "Январь";
        case 2:
            return "Февраль";
        case 3:
            return "Март";
        case 4:
            return "Апрель";
        case 5:
            return "Май";
        case 6:
            return "Июнь";
        case 7:
            return "Июль";
        case 8:
            return "Август";
        case 9:
            return "Сентябрь";
        case 10:
            return "Октябрь";
        case 11:
            return "Ноябрь";
        case 12:
            return "Декабрь";
    };
}


// TODO: все проверки на пустоту через эту функцию
export function isEmpty(data, type = "arr") {
    if (data === null || data === undefined)
        return true;

    if (type === "arr") {
        let result = data.map((item) => {
            return !item.replace(/\s/g, '').length;
        })
        return result.includes(true)
    }

    return !data.replace(/\s/g, '').length;
}


export function isDuplicatedPlacesExists(setedPrizePlaces) {
    if (setedPrizePlaces === null)
        return false;

    let tempPrizePlaces = [...setedPrizePlaces];
    let result = tempPrizePlaces.map((pPlace) => {
        if (pPlace.includes("-")) {
            let splited = pPlace.split("-");
            return tempPrizePlaces.filter(place => place === pPlace).length === (parseInt(splited[1]) - parseInt(splited[0]) + 1)
        }
        return true;
    })
    return result.includes(false);
}


export function isWrongPrizePlacesOrder(setedPrizePlaces) {
    if (setedPrizePlaces === null)
        return false;

    let tempPrizePlaces = [...setedPrizePlaces];
    let resultArr = tempPrizePlaces.map((pPlace, index) => {
        if (pPlace.includes("-")) {
            let spliterPPlace = pPlace.split("-");
            if (!spliterPPlace[1].replace(/\s/g, '').length)
                return false;
            if (parseInt(spliterPPlace[0]) >= parseInt(spliterPPlace[1]))
                return false;
        }
        return checkIsCorrectOrder(index, pPlace, setedPrizePlaces);
    })
    return resultArr.includes(false);
}


function checkIsCorrectOrder(index, str, setedPrizePlaces) {
    let tempPrizePlaces = [...setedPrizePlaces];
    if (index === 0) {
        return true
    } else {
        let str2 = tempPrizePlaces[index - 1];
        let splited2 = str2.split("-");

        let splited = str.split("-");
        if (str.includes("-") && str2.includes("-")) {
            return ((parseInt(splited2[0]) === parseInt(splited[0]) && parseInt(splited2[1]) === parseInt(splited[1])) || (parseInt(splited2[1]) < parseInt(splited[0]) && (parseInt(splited[0]) - parseInt(splited2[1]) === 1)))
        } else if (str.includes("-") && !str2.includes("-") || !str.includes("-") && !str2.includes("-")) {
            return ((parseInt(splited[0]) > parseInt(splited2[0])) && parseInt(splited[0]) - parseInt(splited2[0]) === 1);
        } else if (!str.includes("-") && str2.includes("-")) {
            return ((parseInt(splited[0]) > parseInt(splited2[1])) && parseInt(splited[0]) - parseInt(splited2[1]) === 1);
        }
    }
}


export function getPrizePlaceEnding(place) {
    if (place.endsWith("3") && !place.endsWith("13"))
        return `${place}е`;
    return `${place}ое`;
}


export function fixTagLength(tag) {
    if (tag.length > 8) {
        return `${tag.substring(0, 6)}..`;
    }

    return tag;
}


export async function getTeamSrc(team, partType) {
    if (partType === "player" && team !== "") {
        return await getImage(team)
    }
    return undefined;
}


export function isInt(str) {
    return !isNaN(str) && !isNaN(parseInt(str))
}


export function resetRef(ref) {
    if (ref !== "") {
        ref.current.value = "";
    }
}


export function toggleOffSelectors(toggleDict, idxArr) {
    for (let i = 0; i < idxArr.length; ++i) {
        toggleDict[idxArr[i]](false);
    }
}


export function getEventDate(date) {
    let splitDate = date.split(" - ");
    let splitDateStart = splitDate[0].split(".");
    let splitDateEnd = splitDate[1].split(".");
    if (splitDateStart.length === 3 && splitDateStart[2].length === 4) {
        return `${splitDateStart[0]}.${splitDateStart[1]}.${splitDateStart[2].substring(2)} - ${splitDateEnd[0]}.${splitDateEnd[1]}.${splitDateEnd[2].substring(2)}`;
    } else
        return date;
}