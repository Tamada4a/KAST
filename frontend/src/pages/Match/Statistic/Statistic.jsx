import React from "react";
import "./Statistic.css"
import TeamBlock from "./TeamBlock/TeamBlock";
import { useState } from "react";

function Statistic({ firstTeam, secondTeam, maps, partType }) {
    const [toggleState, setToggleState] = useState(1);


    function toggleTab(index) { // функция toggle для табов
        setToggleState(index);
    };


    function generateTabs() {
        let content = [];

        content.push(
            <div className="tab_map" key={"tab_map/0"}>
                <button className={toggleState === 1 ? "active_tab" : "tab"}
                    onClick={() => toggleTab(1)}>Все карты</button>
            </div>
        );

        for (let i = 0; i < maps.length; ++i) {
            if (maps[i].status === "ended") {
                content.push(
                    <div className="tab_map" key={`tab_map/${i + 1}`}>
                        <button className={toggleState === (i + 2) ? "active_tab" : "tab"}
                            onClick={() => toggleTab((i + 2))}>{maps[i].mapName}</button>
                    </div>
                );
            }
        }

        return content;
    }


    function generateTabsContent() {
        let content = [];

        content.push(
            <div className={toggleState === 1 ? "statictic_content active_statictic_content" : "statictic_content"} key={"tab_map_stat/0"}>
                <TeamBlock team={firstTeam} players={getFullStats(firstTeam.name)} partType={partType} />
                <TeamBlock team={secondTeam} players={getFullStats(secondTeam.name)} partType={partType} />
            </div>
        );

        for (let i = 0; i < maps.length; ++i) {
            if (maps[i].status === "ended") {
                content.push(
                    <div className={toggleState === (i + 2) ? "statictic_content active_statictic_content" : "statictic_content"} key={`tab_map_stat/${i + 1}`}>
                        <TeamBlock team={firstTeam} players={getPlayersByTeamName(firstTeam.name, maps[i].stats)} partType={partType} />
                        <TeamBlock team={secondTeam} players={getPlayersByTeamName(secondTeam.name, maps[i].stats)} partType={partType} />
                    </div>
                );
            }
        }

        return content;
    }


    function getFullStats(teamName) {
        let counter = 1;

        let team = getStatsByTeamName(teamName, maps[0].stats);

        let stats = [...maps[0].stats[team]];

        if (maps.length > 1) {
            for (let i = 1; i < maps.length; ++i) {
                if (maps[i].status === "ended") {
                    counter += 1;
                    for (let j = 0; j < stats.length; ++j) {
                        stats[j] = {
                            ...stats[j],
                            kills: stats[j].kills + maps[i].stats[team][j].kills,
                            assists: stats[j].assists + maps[i].stats[team][j].assists,
                            deaths: stats[j].deaths + maps[i].stats[team][j].deaths,
                            avg: stats[j].avg + maps[i].stats[team][j].avg
                        }
                    }
                }
            }
        }

        for (let i = 0; i < stats.length; ++i) {
            stats[i] = {
                ...stats[i],
                avg: stats[i].avg / counter
            }
        }

        stats.sort((a, b) => getKD(b.kills, b.deaths) - getKD(a.kills, a.deaths));

        return stats;
    }


    function getStatsByTeamName(team, mapStats) {
        if (mapStats.firstTeam === team)
            return "firstTeamPlayers";
        return "secondTeamPlayers";
    }


    function getKD(kills, deaths) {
        return deaths === 0 ? kills : (kills / deaths);
    }


    function getPlayersByTeamName(team, mapStats) {
        return mapStats[getStatsByTeamName(team, mapStats)];
    }


    return (
        <div className="statistic">
            <div className="statistic_maps">
                {generateTabs()}
            </div>
            {generateTabsContent()}
        </div>
    )
}

export default Statistic;