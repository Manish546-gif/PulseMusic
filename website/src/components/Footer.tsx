import { Link } from "react-router-dom";
import { LINKS } from "../lib/links";

export function Footer() {
  const year = new Date().getFullYear();
  return (
    <footer className="site-footer">
      <div className="container">
        <div className="footer-grid">
          <div className="footer-brand">
            <Link to="/" className="brand">
              <img src="/logo.png" alt="" width={34} height={34} />
              <span>
                Pulse <span className="brand-accent">Music</span>
              </span>
            </Link>
            <p>
              A modern Android music app with ad-free streaming, synced lyrics, offline playback,
              and an intuitive experience. Free forever, open source.
            </p>
          </div>
          <div className="footer-col">
            <h4>Product</h4>
            <ul>
              <li>
                <Link to="/#features">Features</Link>
              </li>
              <li>
                <Link to="/download">Download</Link>
              </li>
              <li>
                <Link to="/support">Support the project</Link>
              </li>
            </ul>
          </div>
          <div className="footer-col">
            <h4>Community</h4>
            <ul>
              <li>
                <a href={LINKS.discord} target="_blank" rel="noopener noreferrer">
                  Discord
                </a>
              </li>
              <li>
                <a href={LINKS.github} target="_blank" rel="noopener noreferrer">
                  GitHub
                </a>
              </li>
              <li>
                <a
                  href={`${LINKS.github}/issues`}
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  Issues & requests
                </a>
              </li>
            </ul>
          </div>
          <div className="footer-col">
            <h4>Legal & contact</h4>
            <ul>
              <li>
                <a href={LINKS.hello}>hello@pulsemusic.app</a>
              </li>
              <li>
                <a href={LINKS.security}>security@pulsemusic.app</a>
              </li>
              <li>
                <a href={LINKS.privacy}>Privacy policy</a>
              </li>
              <li>
                <a href={LINKS.license} target="_blank" rel="noopener noreferrer">
                  GPL-3.0 License
                </a>
              </li>
            </ul>
          </div>
        </div>
        <div className="footer-bottom">
          <span>© {year} Pulse Music. Built openly, licensed under GPL-3.0.</span>
          <span>
            Not affiliated with Google, YouTube, or YouTube Music.{" "}
            <a href={LINKS.github} target="_blank" rel="noopener noreferrer">
              Support the source
            </a>
          </span>
        </div>
      </div>
    </footer>
  );
}