import { Routes, Route } from 'react-router-dom'
import Matches from '../../pages/Matches/Matches'
import Tournaments from '../../pages/Tournaments/Tournaments'
import Results from '../../pages/Results/Results'
import Top from '../../pages/Top/Top'
import Player from '../../pages/Player/Player'
import Event from '../../pages/Event/Event'
import Team from '../../pages/Team/Team'
import SocialAuth from "../../pages/SocialAuth/SocialAuth"
import NotFoundPage from '../../pages/NotFoundPage/NotFoundPage'
import AboutUs from '../../pages/AboutUs/AboutUs'

import TeamResults from '../../pages/Team/TeamResults'
import Match from '../../pages/Match/Match'

import TeamEvents from '../../pages/Team/TeamEvents'
import PlayerResults from '../../pages/Player/PlayerResults'
import PlayerEvents from '../../pages/Player/PlayerEvents'
import './Main.css'

function Main() {
    return (
        <main>
            <Routes>
                <Route path="/tournaments/*" element={<Tournaments />} />
                <Route exact path="/" element={<Matches />} />
                <Route path="/results" element={<Results />} />
                <Route path="/top" element={<Top />} />
                <Route path="/player/:id/*" element={<Player />} />
                <Route path="/team/:id/*" element={<Team />} />
                <Route path="/event/:id/*" element={<Event />} />
                <Route path="/team-results/:id" element={<TeamResults />} />
                <Route path="/match/:id/:name" element={<Match />} />
                <Route path="/team-events/:id" element={<TeamEvents />} />
                <Route path="/player-results/:id" element={<PlayerResults />} />
                <Route path="/player-events/:id" element={<PlayerEvents />} />
                <Route path="/social-auth/:id/*" element={<SocialAuth />} />
                <Route path="/about-us" element={<AboutUs />} />
                <Route path="*" element={<NotFoundPage />} />
            </Routes>
        </main>
    );
}

export default Main;