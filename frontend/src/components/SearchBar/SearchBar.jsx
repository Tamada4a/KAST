import React, { useState, useEffect, useRef } from "react";
import { Link } from "react-router-dom";
import { applHeaders, request } from "../../Utils/MyAxios";
import { getImage, fillSpaces } from "../../Utils/Utils";
import "./SearchBar.css";

function SearchBar() {
    const [data, setData] = useState(null);

    const [filteredData, setFilteredData] = useState(null);
    const [isSearchClicked, setIsSearchClicked] = useState(false);
    const wrapperRef = useRef(null);

    const keyTranslate = {
        "events": "Турниры",
        "players": "Игроки",
        "teams": "Команды"
    }


    useEffect(() => {
        getData();
    }, []);


    async function getData() {
        let searchData = await request("GET", "/getSearchData", {}, applHeaders);
        Object.entries(searchData.data).forEach(async ([key, value]) => {
            searchData.data[key] = await Promise.all(value.map(async value => ({
                ...value,
                src: await getImage(value.name)
            })));
        });
        setData(searchData.data);
    }


    function useOutsideAlerter(ref) {
        useEffect(() => {
            function handleClickOutside(event) {
                if (ref.current && !ref.current.contains(event.target)) {
                    setIsSearchClicked(false);
                }
            }
            document.addEventListener("mousedown", handleClickOutside);
            return () => {
                document.removeEventListener("mousedown", handleClickOutside);
            };
        }, [ref]);
    }


    function handleInputChanges(text) {
        let content = [];

        if (data !== null) {
            Object.entries(data).forEach(([key, value]) => {
                let filteredValue = value.filter((item) => item.name.toLowerCase().includes(text.toLowerCase()));

                if (filteredValue.length !== 0) {
                    content.push(
                        <li className="search-bar-list-elem search-bar-list-elem-label non_selectable" key={`SearchLabel${key}`}>
                            <p>{keyTranslate[key]}</p>
                        </li>
                    )

                    filteredValue.map((val) => {
                        content.push(
                            <Link to={getLink(key, val.name)} target="_blank" rel="noopener noreferrer" style={{ textDecoration: "none" }} key={val.name}>
                                <li className="search-bar-list-elem" key={`SearchItem${val.name}`}>
                                    <img src={val.src} alt={val.name} />
                                    <p>{val.name}</p>
                                </li>
                            </Link>
                        );
                    })
                }
            });

            if (content.length === 0 && text !== "") {
                content.push(
                    <li className="search-bar-list-elem search-bar-list-elem-label non_selectable" key="EmptySearch">
                        <p>Ничего нет</p>
                    </li>
                )
            }

            if (text === "") {
                content = [];
            }
        }

        setFilteredData(content);
    }


    function getLink(key, name) {
        return `/${key.substring(0, key.length - 1)}/${fillSpaces(name)}`;
    }


    useOutsideAlerter(wrapperRef);

    return (
        <div className="search-bar-list-wrapper" onClick={e => { setIsSearchClicked(true) }} ref={wrapperRef}>
            <div className="search-bar-wrapper">
                <input type="text" className="search-bar" placeholder="Поиск" onChange={e => { handleInputChanges(e.target.value) }} />
                <div className="search-icon"><img src="../../img/search.svg" alt="иконка поиска" /></div>
            </div>
            <ul className="search-bar-list">
                {filteredData && isSearchClicked ? filteredData : null}
            </ul>
        </div>
    );
}

export default SearchBar;