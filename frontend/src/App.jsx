import './App.css';
import './variables.css'
import './buttons.css'
import './auto-layout.css'
import Header from './components/Header/Header';
import Main from './components/Main/Main'
import Footer from './components/Footer/Footer';
import { Toaster } from "react-hot-toast";

function App() {
  return (
    <>
      <Header />
      <div className="Plashka"></div> {/* Часть, разделяющая Хэдер и Основную часть  */}
      <Main />
      <Footer />
      <Toaster position="top-right" reverseOrder={false} containerStyle={{ marginTop: "84px" }} toastOptions={{ style: { padding: 0, margin: 0 } }}></Toaster>
    </>
  );
}

export default App;
