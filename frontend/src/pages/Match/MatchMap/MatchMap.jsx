import React from "react";
import DrawMatchMap from "./DrawMatchMap";
import MatchMapScoreMaker from "./MatchMapScoreMaker";
import "./MatchMap.css"

function MatchMap({ logoFirst, nameFirst, logoSecond, nameSecond, map, pickedBy, partType }) {
  const eventMapPool = ["Ancient", "Anubis", "Cache", "Cobblestone", "Dust", "Dust2", "Inferno", "Mirage", "Nuke", "Overpass", "Train", "Vertigo"]


  function getScoreColor(side) {
    return { color: side === "CT" ? "var(--ct-color)" : "var(--t-color)" }
  }


  function getScoreByName(name) {
    if (map.firstHalf === null && map.secondHalf === null && map.overtime)
      return null;

    let score = 0;

    if (name === nameFirst) {
      if (map.firstHalf !== null)
        score += map.firstHalf.scoreFirst;

      if (map.secondHalf !== null)
        score += map.secondHalf.scoreFirst;

      if (map.overtime !== null)
        score += map.overtime.scoreFirst;
    } else if (name === nameSecond) {
      if (map.firstHalf !== null)
        score += map.firstHalf.scoreSecond;

      if (map.secondHalf !== null)
        score += map.secondHalf.scoreSecond;

      if (map.overtime !== null)
        score += map.overtime.scoreSecond;
    }

    return map.firstHalf === null ? null : score;
  }


  function getScoreStyle(score1, score2) {
    if (map.firstHalf === null || score1 === score2)
      return { color: "var(--base-14)", fontFamily: "var(--text-regular-lcg)" };

    if (score2 > score1)
      return { color: "red", fontFamily: "var(--text-regular-lcg)" };

    return { color: "green" };
  }


  function getMapOpacity() {
    // console.log(map)
    // if (map.mapName === currentMap)
    //   return null;
    // if (map.firstHalf === null && map.mapName !== "TBA")
    //   return 0.3;
    // return null
    if (map.status === "upcoming" && map.mapName !== "TBA")
      return 0.3;
    return null;
  }


  return (
    <div className={map.mapName === "TBA" ? "map_upcoming" : "map"} style={{ opacity: getMapOpacity() }} >

      <DrawMatchMap eventMapPool={eventMapPool} mapName={map.mapName} />

      {map.mapName === "TBA" ? null :
        <div className="map_points">
          <MatchMapScoreMaker
            styleMain={{ opacity: map.firstHalf !== null && (getScoreByName(nameSecond) > getScoreByName(nameFirst)) ? "0.3" : null }}
            logo={logoFirst}
            styleChild={{ alignItems: "flex-start" }}
            nameClass={"name_first_team"}
            name={nameFirst}
            nameStyle={{ fontFamily: (map.firstHalf !== null && (getScoreByName(nameSecond) >= getScoreByName(nameFirst))) || map.firstHalf === null ? "var(--text-regular-lcg)" : null }}
            scoreClass={"score_first_team"}
            colorStyle={getScoreStyle(getScoreByName(nameFirst), getScoreByName(nameSecond))}
            score={map.firstHalf !== null ? getScoreByName(nameFirst) : null}
            isPicked={pickedBy === nameFirst}
            partType={partType}
          />

          {map.firstHalf !== null ?
            <div className="map_score">
              <p>(</p>
              {map.firstHalf !== null ? <p style={getScoreColor(map.firstHalf.sideFirst)}>{map.firstHalf.scoreFirst}</p> : <p>-</p>}
              <p>:</p>
              {map.firstHalf !== null ? <p style={getScoreColor(map.firstHalf.sideSecond)}>{map.firstHalf.scoreSecond}</p> : <p>-</p>}
              <p>;&nbsp;</p>
              {map.secondHalf !== null ? <p style={getScoreColor(map.secondHalf.sideFirst)}>{map.secondHalf.scoreFirst}</p> : <p>-</p>}
              <p>:</p>
              {map.secondHalf !== null ? <p style={getScoreColor(map.secondHalf.sideSecond)}>{map.secondHalf.scoreSecond}</p> : <p>-</p>}
              <p>)</p>
              {map.overtime !== null ? <p>&nbsp;(</p> : <></>}
              {map.overtime !== null ? map.overtime.scoreFirst !== null ? <p>{map.overtime.scoreFirst}</p> : <p>-</p> : <></>}
              {map.overtime !== null ? <p>:</p> : <></>}
              {map.overtime !== null ? map.overtime.scoreSecond !== null ? <p>{map.overtime.scoreSecond}</p> : <p>-</p> : <></>}
              {map.overtime !== null ? <p>)</p> : <></>}
            </div>
            :
            <></>
          }

          <MatchMapScoreMaker
            styleMain={{ opacity: map.firstHalf !== null && (getScoreByName(nameSecond) < getScoreByName(nameFirst)) ? "0.3" : null, flexDirection: "row-reverse" }}
            logo={logoSecond}
            styleChild={{ alignItems: "flex-end" }}
            nameClass={"name_second_team"}
            name={nameSecond}
            nameStyle={{ fontFamily: (map.firstHalf !== null && (getScoreByName(nameSecond) <= getScoreByName(nameFirst))) || map.firstHalf === null ? "var(--text-regular-lcg)" : null }}
            scoreClass={"score_second_team"}
            colorStyle={getScoreStyle(getScoreByName(nameSecond), getScoreByName(nameFirst))}
            score={map.firstHalf !== null ? getScoreByName(nameSecond) : null}
            isPicked={pickedBy === nameSecond}
            partType={partType}
          />
        </div>
      }
    </div>
  )
}

export default MatchMap;