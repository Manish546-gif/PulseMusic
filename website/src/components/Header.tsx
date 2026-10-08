import { Link } from "react-router-dom";
import { LINKS } from "../lib/links";

export function Header() {
  return (
    <header className="site-header">
      <div className="container header-inner">
        <Link to="/" className="brand" aria-label="Pulse Music — home">
          <img src="/logo.png" alt="" width={34} height={34} />
          <span>
            Pulse <span className="brand-accent">Music</span>
          </span>
        </Link>
        <nav className="nav" aria-label="Primary">
          <a className="nav-link" href="/#features">
            Features
          </a>
          <Link className="nav-link" to="/download">
            Download
          </Link>
          <Link className="nav-link" to="/support">
            Support
          </Link>
        </nav>
        <div style={{ display: "flex", gap: 10, alignItems: "center" }}>
          <a
            className="btn btn-ghost header-cta nav-sm-hide"
            href={LINKS.github}
            target="_blank"
            rel="noopener noreferrer"
          >
            GitHub
          </a>
          <Link className="btn btn-primary header-cta" to="/download">
            Get Pulse Music
          </Link>
        </div>
      </div>
    </header>
  );
}