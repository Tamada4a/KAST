import React, { useState, useEffect } from "react";
import "./Player.css"
import Login from "../../../components/Login/Login";
import { applHeaders, request } from "../../../Utils/MyAxios";
import SearchableSelector from "../../../components/Selector/SearchableSelector";
import PlayerWrapper from "../../../components/PlayerWrapper/PlayerWrapper";
import { getItemFromDictByValue, showNotification, getImage } from "../../../Utils/Utils";

function Player(props) {
    const player = props.player;

    const [mouseOutCard, setMouseOutCard] = useState(true); //Для ховера игрока
    const [mouseOnCard, setMouseOnCard] = useState(false); //Для ховера игрока

    const [kickActive, setkickActive] = useState(false);

    const [inviteActive, setInviteActive] = useState(false);
    const [inviteSelectorActive, setInviteSelectorActive] = useState(false);
    const [inviteValue, setInviteValue] = useState("Выберите игрока");

    const [players, setPlayers] = useState(null);


    async function getAllPlayers() {
        let allPlayers = await request("GET", "/getPlayersWithoutTeams", {}, applHeaders);
        setPlayers(await Promise.all(allPlayers.data.map(async allPlayer => ({
            ...allPlayer,
            src: await getImage(allPlayer.name)
        }))));
    }


    useEffect(() => {
        getAllPlayers();
    }, []);


    function getHoverImage() {
        if (player.name !== "?") {
            return (<img className="kick_hover" src="../../img/KickHovered.svg" alt="Удаление игрока" />);
        }
        return (<img className="kick_hover" src="../../img/Add.svg" alt="Добавление игрока" />);
    }


    function toggleOnMouseOver() {
        return (
            mouseOutCard ?
                <PlayerWrapper issuer={"team"} src={player.photo} name={player.name} flagPath={player.flagPath} country={player.country} mainStyle={null} />
                :
                <div className="img_hover_wrapper_in_team" onMouseOut={() => { setMouseOutCard(true); setMouseOnCard(false) }} onClick={() => player.name !== "?" ? setkickActive(true) : onInviteHandler()}>
                    <PlayerWrapper issuer={"team"} src={player.photo} name={player.name} flagPath={player.flagPath} country={player.country} mainStyle={{ opacity: "0.4" }} />
                    {getHoverImage()}
                </div>
        );
    }


    function onInviteHandler() {
        setInviteSelectorActive(false);
        setInviteValue("Выберите игрока");
        setInviteActive(true);
    }


    function removePlayer(rmvPlayer) {
        let teamInfo = props.teamInfo;
        teamInfo.players = teamInfo.players.filter(player => !(player.name.includes(rmvPlayer.name)));

        props.updateTeamInfo(teamInfo);
        props.updateExPlayers(props.ex_players === null ? [rmvPlayer] : [...props.ex_players, rmvPlayer]);

        request("POST", "/leftTeam",
            {
                nick: rmvPlayer.name,
                team: teamInfo.name,
                isKick: true
            });
    }


    async function sendInvite() {
        try {
            let resp = await request("POST", `/sendTeamInvite/${props.teamInfo.name}/${inviteValue}`, {});
            showNotification(resp.data, "ok");
        } catch (err) {
            showNotification(err.response.data.message, "warn");
        }
    }


    return (
        <div className="team_player_card_wrapper" onMouseOut={() => { setMouseOutCard(true); setMouseOnCard(false) }} onMouseOver={() => { setMouseOutCard(false); setMouseOnCard(true) }}>
            {props.isCap && props.curPlayer !== player.name ? toggleOnMouseOver() :
                <PlayerWrapper issuer={"team"} src={player.photo} name={player.name} flagPath={player.flagPath} country={player.country} mainStyle={null} />
            }

            {props.isCap ?
                <Login active={kickActive} setActive={setkickActive}>
                    <div className="header_splash_window">
                        <div className="logo_splash_window"></div>
                    </div>
                    <div className="info_text">
                        <p>Вы уверены, что хотите исключить игрока {player.name}?</p>
                    </div>
                    <div className="small_buttons_wrapper">
                        <div className="small_dark_button">
                            <input type="submit" value="Нет" onClick={() => kickActive ? setkickActive(!kickActive) : null} />
                        </div>
                        <div className="small_grey_button">
                            <input type="submit" value="Да" onClick={() => { removePlayer(player); setkickActive(false) }} />
                        </div>
                    </div>
                </Login>
                :
                <></>
            }

            {props.isCap ?
                <Login active={inviteActive} setActive={setInviteActive}>
                    <div className="header_splash_window">
                        <div className="logo_splash_window"></div>
                    </div>
                    <div className="info_text">
                        <p>Выберите игрока, которого хотите пригласить</p>
                    </div>
                    {players !== null ?
                        <div className="col_center_gap30">
                            <SearchableSelector issuer={"eventSelector"} type={"half"} styleMain={{ zIndex: 2 }} styleItems={null} setSelectorActive={setInviteSelectorActive} value={inviteValue} startValue={"Выберите игрока"} selectorActive={inviteSelectorActive} data={players} setValue={setInviteValue} itemKey={"name"} srcKey={"src"} />
                            {getItemFromDictByValue(players, "name", inviteValue) !== undefined ?
                                <div className="full_grey_button">
                                    <input type="submit" value="Подтвердить" onClick={() => { sendInvite(); setInviteActive(false) }} />
                                </div>
                                :
                                <></>
                            }
                        </div>
                        :
                        <></>
                    }
                </Login>
                :
                <></>
            }
        </div>
    );
}

export default Player;