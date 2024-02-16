import React from "react";
import "./PlayerWrapper.css"
import FlagName from "../FlagName/FlagName";

function PlayerWrapper(props) {
    function getMainStyle() {
        switch (props.issuer) {
            case "match": case "top": case "structure":
                return { cursor: props.name !== "TBA" ? "pointer" : "auto" };
            case "team": case "statistic": case "scoreBoard":
                return props.mainStyle;
        }
    }


    function getMainClass() {
        switch (props.issuer) {
            case "match":
                return "match_roster_player";
            case "top":
                return "player_top";
            case "structure":
                return "player_top nick_roster";
            case "team":
                return null;
            case "statistic":
                return "statistic_team_row_name";
            case "scoreBoard":
                return "team_row_name";
        }
    }


    function getSecondClass() {
        switch (props.issuer) {
            case "match":
                return "match_player_roster_back";
            case "top":
                return "players_background";
            case "structure":
                return "players_background_roster";
            case "team":
                return "players_team";
            case "statistic": case "scoreBoard":
                return "player_wrapper_23px";
        }
    }


    function getThirdClass() {
        switch (props.issuer) {
            case "match":
                return "match_player_roster_crop";
            case "top":
                return "crop";
            case "structure":
                return "crop_roster";
            case "team":
                return "crop_team";
            case "statistic": case "scoreBoard":
                return "player_wrapper_23px_crop";
        }
    }


    function getDraw() {
        switch (props.issuer) {
            case "match": case "top": case "structure":
                return (
                    props.name !== "TBA" ?
                        <FlagName flagPath={props.flagPath} country={props.country} height={getFlagHeight()} name={props.name} isWrap={true} width={getTextWitdh()} />
                        :
                        <p>TBA</p>
                );
            case "team":
                return (
                    <div className="nick_team">
                        {props.name !== "?" ? <img src={props.flagPath} alt={props.country} /> : <></>}
                        <p>{props.name}</p>
                    </div>
                );
            case "statistic": case "scoreBoard":
                return (
                    <p>{props.name}</p>
                );
        }
    }


    function getTextWitdh() {
        switch (props.issuer) {
            case "match":
                return "110px";
            case "top":
                return "127px";
            case "structure":
                return "91px";
        }
    }


    function getFlagHeight() {
        switch (props.issuer) {
            case "match":
                return "13px";
            case "top": case "structure":
                return "12px";
        }
    }


    return (
        <div className={getMainClass()} style={getMainStyle()}>
            <div className={getSecondClass()}>
                <div className={getThirdClass()}>
                    <img src={props.src} alt={props.name} />
                </div>
            </div>
            {getDraw()}
        </div>
    )
}

export default PlayerWrapper;