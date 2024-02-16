import React from "react";
import "./Stats.css"

function Stats(props) {
    let kd = props.stats.deaths === 0 ? props.stats.kills : (props.stats.kills / props.stats.deaths).toFixed(2);
    let kpm = props.stats.maps === 0 ? props.stats.kills : (props.stats.kills / props.stats.maps).toFixed(2);
    let kdd = props.stats.kills - props.stats.deaths;
    let hsp = props.stats.kills === 0 ? props.stats.hsKills : (props.stats.hsKills / props.stats.kills).toFixed(2);
    let dpr = props.stats.roundsPlayed === 0 ? props.stats.fullDamage : (props.stats.fullDamage / props.stats.roundsPlayed).toFixed(2);

    return (
        <div>
            {props.stats !== null ?
                <div className="stats_box">
                    <div className="devider_stats">
                        <div className="devider_stats_line">
                            <span>Всего убийств</span>
                            <p>{props.stats.kills}</p>
                        </div>
                        <div className="devider_subline"></div>
                    </div>

                    <div className="devider_stats">
                        <div className="devider_stats_line">
                            <span>Всего смертей</span>
                            <p>{props.stats.deaths}</p>
                        </div>
                        <div className="devider_subline"></div>
                    </div>

                    <div className="devider_stats">
                        <div className="devider_stats_line">
                            <span>Убийства / Смерти</span>
                            <p style={{ color: kd > 1 ? 'var(--base-11)' : kd < 0 ? 'red' : 'white' }}>{kd}</p>
                        </div>
                        <div className="devider_subline"></div>
                    </div>

                    <div className="devider_stats">
                        <div className="devider_stats_line">
                            <span>Убийства - Смерти</span>
                            <p style={{ color: kdd > 0 ? 'var(--base-11)' : kdd < 0 ? 'red' : 'white' }}>{kdd}</p>
                        </div>
                        <div className="devider_subline"></div>
                    </div>

                    <div className="devider_stats">
                        <div className="devider_stats_line">
                            <span>Убийства в голову %</span>
                            <p style={{ color: hsp > 50 ? 'var(--base-11)' : 'red' }}>{hsp}</p>
                        </div>
                        <div className="devider_subline"></div>
                    </div>

                    <div className="devider_stats">
                        <div className="devider_stats_line">
                            <span>Урон / Раунд</span>
                            <p style={{ color: dpr > 60 ? 'var(--base-11)' : 'red' }}>{dpr}</p>
                        </div>
                        <div className="devider_subline"></div>
                    </div>

                    <div className="devider_stats">
                        <div className="devider_stats_line">
                            <span>Сыграно раундов</span>
                            <p>{props.stats.roundsPlayed}</p>
                        </div>
                        <div className="devider_subline"></div>
                    </div>

                    <div className="devider_stats">
                        <div className="devider_stats_line">
                            <span>Сыграно карт</span>
                            <p>{props.stats.maps}</p>
                        </div>
                        <div className="devider_subline"></div>
                    </div>

                    <div className="devider_stats">
                        <div className="devider_stats_line">
                            <span>Убийства / Матч</span>
                            <p style={{ color: kpm >= 17 ? 'var(--base-11)' : 'red' }}>{kpm}</p>
                        </div>
                    </div>
                </div>
                :
                <></>
            }
        </div>
    );
}

export default Stats;