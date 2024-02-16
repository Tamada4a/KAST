import React, { useRef } from "react";
import { onImageUploaded, showNotification } from "../../../Utils/Utils";
import "./InfoContainer.css"

function InfoContainer(props) {
  async function handleImageUploaded(event) {
    let file = event.target.files[0];
    if (file !== null && file !== "null" && file !== "undefined" && file !== undefined) {
      if (file.size > 10485760) {
        showNotification("Размер фотографии не может быть больше 10мб", "warn");
      } else {
        try {
          await onImageUploaded(file, "/teams_logo/", props.name);
          props.setTeamLogo(URL.createObjectURL(file));
          showNotification("Логотип успешно обновлён", "ok");
        } catch (err) {
          showNotification("При обновлении логотипа произошла ошибка", "warn");
        }
      }
    }
  }


  function ImageToggleOnMouseOver({ primaryImg, secondaryImg, alt }) {
    const imageRef = useRef(null);

    return (
      <img
        onMouseOver={() => {
          imageRef.current.src = secondaryImg;
          imageRef.current.className = "hovered";
        }}
        onMouseOut={() => {
          imageRef.current.src = primaryImg;
          imageRef.current.className = "";
        }}
        src={primaryImg}
        alt={alt}
        ref={imageRef}
        className=""
      />
    )
  }


  function showLogo() {
    return (
      props.isCapAdmin ?
        <div className="team_logo">
          <label htmlFor="file-input">
            <ImageToggleOnMouseOver
              primaryImg={props.teamLogo}
              secondaryImg={"../../img/PhotoHover.svg"}
              alt={props.name}
            />
          </label>
          <input id="file-input" type="file" accept="image/*" onChange={handleImageUploaded} />
        </div>
        :
        <div className="team_logo">
          <img src={props.teamLogo} alt={props.name} />
        </div>
    );
  }


  return (
    <div className="info_container">
      <div className="team_info">
        {showLogo()}
        <div className="info_teamname">
          <div className="flag_location">
            <img src={props.flagPath} alt={props.country} />
            {props.city !== "" ? <p>{props.country}, {props.city}</p> : <p>{props.country}</p>}
          </div>
          <p>{props.name}</p>
        </div>
      </div>
      <div className="top_position">
        <p>Место в рейтинге</p>
        {props.topPosition !== -999 ? <p>#{props.topPosition}</p> : <p>-</p>}
      </div>
    </div>
  )
}

export default InfoContainer;