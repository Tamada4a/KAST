import React from "react";
import './Editor.css';

function Editor(props) {

    const size = props.size;


    function getDepth() {
        let path = "";
        for (let i = 0; i < props.depth; ++i) {
            path += "../";
        }
        return path;
    }


    return (
        <div className="editor" style={{ width: size, height: size }} onClick={props.onClick}>
            <img src={`${getDepth()}img/Editor.svg`} alt="Editor" />
        </div>
    );
}

export default Editor;