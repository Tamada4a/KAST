import React from "react";
import { Link } from "react-router-dom";
import { fillSpaces } from "../../Utils/Utils";
import "./Trophies.css"

function Trophies(props) {
    return (
        <div>
            {props.items !== null && props.items.length > 0 ?
                <div className="trophies_container">
                    <div id="trophies">
                        {props.items.map((item, i) =>
                            <Link to={`/event/${fillSpaces(item.name)}`} style={{ textDecoration: "none" }} key={`${item.name}/${i}`}>
                                <img src={item.src} alt={item.name} title={item.name} />
                            </Link>
                        )
                        }
                    </div>
                </div>
                :
                <></>
            }
        </div>
    );
}
export default Trophies;
