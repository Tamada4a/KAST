import React from "react";

function NonSelectableSelector(props) {
    return (
        <div className={props.type === "third" ? "text-field_third" : "text-field_half"} style={props.styleMain}>
            <div className={props.type === "third" ? "text-field_third_selector" : "text-field_half_selector"}>
                <div className={props.type === "third" ? "text_field_third_select" : "text_field_half_select"} style={{ cursor: "default" }}>
                    <p className="choosed" style={props.styleP}>{props.text}</p>
                </div>
            </div>
        </div>
    )
}

export default NonSelectableSelector;