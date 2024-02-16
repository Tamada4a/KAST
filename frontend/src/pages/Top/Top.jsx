import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom"
import NonFirstTeam from "./Teams/NonFirstTeam/NonFirstTeam";
import FirstTeam from "./Teams/FirstTeam/FirstTeam";
import Editor from "../../components/Editor/Editor";
import Login from "../../components/Login/Login";
import SearchableArraySelector from "../../components/Selector/SearchableArraySelector";
import NonSelectableSelector from "../../components/Selector/NonSelectableSelector";
import { getStoredPlayerNick, fillSpaces, indexOf, getItemFromDictByValue, fixNumbers, getImage, isNotInList, showNotification } from "../../Utils/Utils";
import { applHeaders, request } from "../../Utils/MyAxios";
import "./Top.css"
import Preloader from "../../components/Preloader/Preloader";


function Top() {

  const [topTeams, setTopTeams] = useState(null);

  const [topDate, setTopDate] = useState(null);

  const [isAdmin, setIsAdmin] = useState(false);

  const [editorActive, setEditorActive] = useState(false); //состояния модального окна для редактирования топа
  const [selectorActive, setSelectorActive] = useState([false, false, false, false, false]); // состояния селектора

  const [valueTeam, setValueTeam] = useState(['Выберите команду', 'Выберите команду', 'Выберите команду', 'Выберите команду', 'Выберите команду']); //Для селектора команды

  const [teams, setTeams] = useState(null);

  const [teamsActive, setTeamsActive] = useState([false, false, false, false, false]); // состояния команд - выбрана ли команда(чтоб блокировать ее)

  async function getPlayers(playersData) {
    let players = await Promise.all(playersData.map(async player => ({
      ...player,
      photo: await getImage(player.name)
    })))

    if (players.length < 5) {
      return players.concat(Array(5 - players.length).fill({ name: "TBA", photo: "img/players/NonPhoto.png" }));
    }
    return players;
  }


  async function getFullTop() {
    const top = await request("GET", "/getFullTop", {}, applHeaders);

    let topTeams_ = await Promise.all(top.data.topTeams.map(async team => ({
      ...team,
      logo: await getImage(team.name),
      players: await getPlayers(team.players)
    })));

    if (topTeams_.length > 5)
      setTopTeams(topTeams_.slice(0, 5));
    else
      setTopTeams(topTeams_);

    setTeams(topTeams_);

    setTopDate(top.data.topDate);
  }


  async function getIsAdmin() {
    setIsAdmin(await request("GET", `/isAdmin/${getStoredPlayerNick()}`, {}, applHeaders).then(res => res.data));
  }


  useEffect(() => {
    getFullTop();
    getIsAdmin();
  }, []);


  function toggleClass(id) { // функция toggle для селектора
    let temp = [];
    for (let i = 0; i < selectorActive.length; ++i) {
      temp.push(false);
    }
    if (id !== "all") {
      temp[id] = !selectorActive[id];
    }
    setSelectorActive(temp);
  };


  function setTeam(id, value) { // с её помощью делаем нужную команду заблокированной 
    let temp = [...teamsActive];
    let val = indexOf(value, teams, "name");

    temp[val] = !temp[val];
    temp[id] = !temp[id];

    setTeamsActive(temp);
  }


  function setTeamValue(id, value) { // ставим выбранную команду в выбранное поле
    let tempTeams = [...valueTeam];
    tempTeams[id] = value;
    setValueTeam(tempTeams);
  }


  function generateSelectors() {
    let content = []
    for (let i = 0; i < 5; ++i) {
      content.push(
        <div className="row_center_6" key={`top_change_position${i}`}>
          <SearchableArraySelector issuer={"eventPlayerSelector"} styleMain={{ zIndex: 5 - i }} startValue={"Выберите команду"} value={valueTeam[i]} selectorActive={selectorActive[i]} toggleClass={toggleClass} index={i} data={teams} itemKey={"name"} srcKey={"logo"} setDataValue={setTeamValue} setItem={setTeam} arrayActive={teamsActive} type={"half"} />
          <NonSelectableSelector type={"half"} styleMain={{ zIndex: 5 - i }} text={i + 1} styleP={null} />
        </div>
      );
    }
    return content;
  }


  function isAllInList() {
    let result = valueTeam.map((team) => {
      if (isNotInList(teams, "name", team) && team !== "Выберите команду")
        return false;
    })
    return !result.includes(false);
  }


  function isAllUnique() {
    let count = valueTeam.map((team) => {
      return valueTeam.filter(fteam => team === fteam).length;
    })

    return JSON.stringify(count) === "[1,1,1,1,1]";
  }


  async function onTopEdited() {
    if (valueTeam.includes("Выберите команду") || valueTeam.includes("")) {
      showNotification("Вы указали не все команды", "warn");
    } else if (!isAllInList()) {
      showNotification("Одной из выбранных команд не существует", "warn");
    } else if (!isAllUnique()) {
      showNotification("Вы указали одинаковые команды", "warn");
    } else {
      let result = valueTeam.map((team, i) => {
        let oldPos = getItemFromDictByValue(teams, "name", team);
        if (oldPos !== undefined) {
          return ({ ...oldPos, changedPosition: oldPos.topPosition - (i + 1), topPosition: i + 1 });
        }
      });

      let outOfTop = topTeams.filter(team => isNotInList(result, "name", team.name)).map((team, i) => ({
        ...team,
        topPosition: team.topPosition > -999 ? 5 + team.topPosition : 5 + i + 1,
        changedPosition: team.topPosition > -999 ? team.topPosition - (i + 6) : -999
      }));

      let date = new Date();
      let dateStr = `${fixNumbers(date.getDate())}.${fixNumbers(date.getMonth() + 1)}.${date.getFullYear()}`;

      try {
        await request("POST", "/setTop", { topDate: dateStr, topTeams: result.concat(outOfTop) });
        setTopTeams(result)
        setTopDate(dateStr);
        setEditorActive(false);
        showNotification("Топ успешно обновлён", "ok");
      } catch (err) {
        showNotification(err.response.data.message, "warn");
      }
    }
  }


  return (
    <div>
      {topDate && topTeams && teams ?
        <div className="top_teams">
          <div className="row_center_5px">
            {topDate !== null ? <p>Топ команд на {topDate}</p> : <></>}
            {isAdmin && topDate !== null ? <Editor size="20px" depth={0} onClick={() => { setEditorActive(true); setSelectorActive([false, false, false, false, false]); setTeamsActive([false, false, false, false, false]); setValueTeam(['Выберите команду', 'Выберите команду', 'Выберите команду', 'Выберите команду', 'Выберите команду']) }} />
              : <></>}

          </div>
          <div className="col_center_gap10">

            {topTeams !== null && topTeams.length > 0 ?
              <FirstTeam {...topTeams[0]} />
              :
              <></>
            }

            <div className="col_center_gap10">
              {topTeams !== null && topTeams.length > 0 ?
                topTeams.slice(1).map((team, i) =>
                  <Link to={`/team/${fillSpaces(team.name)}`} style={{ textDecoration: "none" }} key={team.name}>
                    <NonFirstTeam {...team} topPos={i + 2} />
                  </Link>
                )
                :
                <></>
              }
            </div>
          </div>
        </div>
        :
        <Preloader />
      }

      {isAdmin ?
        <Login active={editorActive} setActive={setEditorActive} key={"editorActive"}>
          <div className="header_splash_window" onClick={() => toggleClass("all")}>
            <div className="logo_splash_window"></div>
          </div>
          <div className="info_text" onClick={() => toggleClass("all")}>
            <p>Укажите информацию о положении команд в топе</p>
          </div>
          <div className="col_center_gap30">
            <div className="inside">
              {teams !== null ? generateSelectors() : <></>}
            </div>
            <div className="full_grey_button">
              <input type="submit" value="Сохранить" onClick={() => { onTopEdited() }} />
            </div>
          </div>
        </Login>
        :
        <></>
      }
    </div>
  )
};

export default Top;