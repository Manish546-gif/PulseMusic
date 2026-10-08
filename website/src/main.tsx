import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import App from "./App";
import { Home } from "./pages/Home";
import { Download } from "./pages/Download";
import { Support } from "./pages/Support";
import { NotFound } from "./pages/NotFound";
import { WatchPage } from "./pages/share/WatchPage";
import { PlaylistPage } from "./pages/share/PlaylistPage";
import { ChannelPage } from "./pages/share/ChannelPage";
import "./styles.css";

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <BrowserRouter>
      <Routes>
        <Route element={<App />}>
          <Route path="/" element={<Home />} />
          <Route path="/download" element={<Download />} />
          <Route path="/support" element={<Support />} />
        </Route>
        <Route path="/watch" element={<WatchPage />} />
        <Route path="/playlist" element={<PlaylistPage />} />
        <Route path="/channel/:id" element={<ChannelPage />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </BrowserRouter>
  </React.StrictMode>
);