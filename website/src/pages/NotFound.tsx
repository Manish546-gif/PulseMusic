import { Link } from "react-router-dom";

export function NotFound() {
  return (
    <div className="notfound">
      <div>
        <div className="display">404</div>
        <h1 className="h1">This track went silent.</h1>
        <p>That page doesn't exist — or the share link is broken. Head back and keep the music going.</p>
        <Link className="btn btn-primary btn-lg" to="/">
          Back to Pulse Music
        </Link>
      </div>
    </div>
  );
}