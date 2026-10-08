import { useEffect } from "react";
import { Outlet, useLocation } from "react-router-dom";
import { Header } from "./components/Header";

function ScrollManager() {
  const location = useLocation();

  useEffect(() => {
    if (location.hash) {
      const id = location.hash.slice(1);
      const el = document.getElementById(id);
      if (el) {
        el.scrollIntoView({ behavior: "instant" });
        return;
      }
    }
    window.scrollTo(0, 0);
  }, [location]);

  return null;
}

export default function App() {
  return (
    <>
      <ScrollManager />
      <Header />
      <Outlet />
    </>
  );
}