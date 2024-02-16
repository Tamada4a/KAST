import React from "react";

function FileLoader(props) {
    return (
        <div className="text-field_half">
            <div className="text-field_half_selector">
                <label htmlFor={`file-input ${props.value}`}>
                    <div className="text_field_half_select">
                        <p className={props.value === props.startValue ? "onStart" : "choosed"}>{props.value}</p>
                        <img src="../../img/Add.svg" alt="Add" style={{ width: "15px" }} />
                    </div>
                </label>
                <input id={`file-input ${props.value}`} type="file" accept="image/*" style={{ all: "unset", width: "0px", height: "0px" }} onChange={(ev) => { props.handleImageUploaded(ev, props.setSelectedFile) }} />
            </div>
        </div>
    )
}

export default FileLoader;