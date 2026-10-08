import { Link } from "react-router-dom";
import { type LucideIcon, Download, Music2, ShieldCheck } from "lucide-react";

interface ShareShellProps {
  kind: string;
  headline: string;
  description: string;
  url: string;
  icon: LucideIcon;
  art?: { src: string; alt: string };
  meta?: string;
}

export function ShareShell({ kind, headline, description, url, icon: Icon, art, meta }: ShareShellProps) {
  return (
    <div className="share-shell">
      <div className="share-top">
        <div className="container share-top-inner">
          <Link to="/" className="brand">
            <img src="/logo.png" alt="" width={30} height={30} />
            <span style={{ fontSize: 16 }}>
              Pulse <span className="brand-accent">Music</span>
            </span>
          </Link>
          <Link className="btn btn-ghost header-cta" to="/download" style={{ minHeight: 40 }}>
            <Download size={16} /> Get the app
          </Link>
        </div>
      </div>

      <div className="share-body">
        <div className="share-card">
          <div className="share-art">
            {art ? <img src={art.src} alt={art.alt} /> : <Icon size={52} strokeWidth={1.5} />}
          </div>
          <span className="eyebrow" style={{ justifyContent: "center" }}>
            {kind}
          </span>
          <h1 className="h1" style={{ fontSize: "clamp(26px, 4vw, 38px)" }}>
            {headline}
          </h1>
          <p className="share-title-line">{description}</p>
          {meta ? <div className="share-meta">{meta}</div> : null}
          <div className="share-actions">
            <a className="btn btn-primary btn-lg" href={url}>
              <Music2 size={18} /> Open in Pulse Music
            </a>
            <Link className="btn btn-ghost btn-lg" to="/download">
              <Download size={18} /> Doesn't have the app? Get it free
            </Link>
          </div>
          <p className="share-note">
            <ShieldCheck size={14} /> On Android with Pulse Music installed, this link routes
            straight into the app. No account or sign-in required.
          </p>
        </div>
      </div>
    </div>
  );
}