import { useState } from "react";
import { Link } from "react-router-dom";
import {
  AudioLines,
  Brain,
  Check,
  Copy,
  Disc3,
  Download,
  ExternalLink,
  ListMusic,
  MessageSquareText,
  Play,
  RefreshCw,
  Sparkles,
  UserRound,
  Waves,
} from "lucide-react";
import { Reveal } from "../components/Reveal";
import { PhoneMockup } from "../components/PhoneMockup";
import { Footer } from "../components/Footer";
import { LINKS, SHARE_URL } from "../lib/links";

const SHARE_EXAMPLES = [
  { icon: Play, label: "Song", url: `${SHARE_URL}/watch?v=<video-id>` },
  { icon: ListMusic, label: "Playlist", url: `${SHARE_URL}/playlist?list=<list-id>` },
  { icon: UserRound, label: "Artist", url: `${SHARE_URL}/channel/<channel-id>` },
];

function ShareUrlRow({
  icon: Icon,
  label,
  url,
  copied,
  onCopy,
}: {
  icon: typeof Play;
  label: string;
  url: string;
  copied: boolean;
  onCopy: () => void;
}) {
  return (
    <button className="share-url" type="button" onClick={onCopy} aria-label={`Copy ${label} share link`}>
      <span>
        <Icon size={15} />
      </span>
      <span style={{ fontFamily: "var(--font-mono)" }}>
        <span className="url-host">{SHARE_URL}</span>
        <em style={{ color: "var(--text-faint)" }}>/</em>
        {url.split("?")[0].split("/").slice(3).join("/")}
        {url.includes("?") ? (
          <em style={{ color: "var(--text-faint)" }}>
            ?{url.split("?")[1].replace(/[<>]/g, "·")}
          </em>
        ) : null}
      </span>
      {copied ? <Check size={15} /> : <Copy size={15} />}
    </button>
  );
}

function SupportCard({
  img,
  alt,
  title,
  desc,
  href,
}: {
  img: string;
  alt: string;
  title: string;
  desc: string;
  href: string;
}) {
  return (
    <a className="support-card" href={href} target="_blank" rel="noopener noreferrer">
      <img src={img} alt={alt} width={48} height={48} />
      <h3 className="h3">{title}</h3>
      <p>{desc}</p>
      <span className="btn btn-ghost">
        Support <ExternalLink size={16} />
      </span>
    </a>
  );
}

export function Home() {
  const [copied, setCopied] = useState<number | null>(null);

  const copy = (index: number, url: string) => {
    void navigator.clipboard?.writeText(url);
    setCopied(index);
    window.setTimeout(() => setCopied(null), 1800);
  };

  return (
    <>
      <main>
        {/* ---------- hero ---------- */}
        <section className="hero">
          <div className="container hero-grid">
            <div>
              <span className="eyebrow">Pulse Music · Android</span>
              <h1 className="h1">
                <span className="line">Your music, streaming</span>
                <span className="line">
                  <span className="accent">clean.</span>
                </span>
              </h1>
              <p className="hero-copy">
                Pulse Music streams the catalog you love without the noise — no ads, no premium
                walls. Synced lyrics, offline downloads, and live song recognition that just work.
                Free forever, and open source.
              </p>
              <div className="hero-ctas">
                <Link className="btn btn-primary btn-lg" to="/download">
                  <Download size={18} /> Download for Android
                </Link>
                <a
                  className="btn btn-ghost-light btn-lg"
                  href={LINKS.discord}
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  Join Discord
                </a>
              </div>
              <p className="hero-note">
                <Sparkles size={16} />
                No accounts. No servers. No ads. Completely free.
              </p>
            </div>
            <Reveal>
              <PhoneMockup />
            </Reveal>
          </div>
        </section>

        {/* ---------- marquee ---------- */}
        <div className="marquee-band" aria-hidden="true">
          <div className="marquee">
            <span>AD-FREE <em>·</em> OPEN SOURCE <em>·</em> SYNCED LYRICS <em>·</em> OFFLINE <em>·</em> PULSE FIND <em>·</em> NO SERVERS <em>·</em> </span>
            <span>AD-FREE <em>·</em> OPEN SOURCE <em>·</em> SYNCED LYRICS <em>·</em> OFFLINE <em>·</em> PULSE FIND <em>·</em> NO SERVERS <em>·</em> </span>
          </div>
        </div>

        {/* ---------- features ---------- */}
        <section className="section" id="features">
          <div className="container">
            <Reveal>
              <div className="chapter">
                <span className="eyebrow">Features</span>
                <h2 className="h2">Everything a music app should be.</h2>
                <p>Not a shell around a web player — a real listening experience.</p>
              </div>
            </Reveal>

            <div className="features-grid">
              <Reveal className="feature-card fc-wide">
                <span className="fc-icon"><AudioLines size={22} /></span>
                <h3 className="h3">Pulse Find</h3>
                <p>
                  Hear a song somewhere and don't know it? Pulse Find listens and identifies it in
                  seconds using advanced on-device audio recognition.
                </p>
                <div className="fc-tags">
                  <span className="tag">Real-time recognition</span>
                  <span className="tag">On-device</span>
                  <span className="tag">No typing</span>
                </div>
              </Reveal>

              <Reveal className="feature-card" delay={80}>
                <span className="fc-icon"><MessageSquareText size={22} /></span>
                <h3 className="h3">Synced lyrics</h3>
                <p>
                  Word-by-word synchronized lyrics with multiple animations and built-in AI
                  translation into any language.
                </p>
                <div className="fc-tags">
                  <span className="tag">Word-by-word</span>
                  <span className="tag">AI translation</span>
                </div>
              </Reveal>

              <Reveal className="feature-card" delay={60}>
                <span className="fc-icon"><RefreshCw size={22} /></span>
                <h3 className="h3">Import from Spotify</h3>
                <p>
                  Bring your playlists over in one tap and keep them in sync with Fast Sync.
                </p>
                <div className="fc-tags">
                  <span className="tag">One-tap import</span>
                  <span className="tag">Keeps updating</span>
                </div>
              </Reveal>

              <Reveal className="feature-card fc-wide" delay={140}>
                <span className="fc-icon"><Brain size={22} /></span>
                <h3 className="h3">Pulse Brain</h3>
                <p>
                  An intelligent, on-device engine that reads your listening momentum and
                  auto-injects perfectly aligned tracks straight into your queue. Flow, uninterrupted.
                </p>
                <div className="fc-tags">
                  <span className="tag">On-device engine</span>
                  <span className="tag">Adaptive queue</span>
                </div>
              </Reveal>

              <Reveal className="feature-card fc-wide" delay={60}>
                <span className="fc-icon"><Waves size={22} /></span>
                <h3 className="h3">Crossfade & canvas</h3>
                <p>
                  Buttery-smooth transitions between tracks, plus animated canvas artwork that
                  plays while you listen — no interrupted vibe ever.
                </p>
                <div className="fc-tags">
                  <span className="tag">Crossfade</span>
                  <span className="tag">Canvas animations</span>
                  <span className="tag">Background playback</span>
                </div>
              </Reveal>

              <Reveal className="feature-card" delay={120}>
                <span className="fc-icon"><Download size={22} /></span>
                <h3 className="h3">Offline mode</h3>
                <p>
                  Download tracks, albums, and playlists with a dedicated download manager. Your
                  music, wherever the signal isn't.
                </p>
                <div className="fc-tags">
                  <span className="tag">Tracks · Albums · Playlists</span>
                  <span className="tag">Data saver</span>
                </div>
              </Reveal>

              <Reveal className="feature-card fc-full">
                <h3 className="h3">Everything else, included.</h3>
                <p className="muted" style={{ marginBottom: 6 }}>
                  No premium tiers, no paywalls — the full feature set ships in one free app.
                </p>
                <div className="fc-tags">
                  <span className="tag">Listen Together</span>
                  <span className="tag">Podcasts</span>
                  <span className="tag">Local media</span>
                  <span className="tag">Dynamic Island</span>
                  <span className="tag">Set as ringtone</span>
                  <span className="tag">Pause on mute</span>
                  <span className="tag">Resume on Bluetooth</span>
                  <span className="tag">Data saver mode</span>
                  <span className="tag">High refresh rate</span>
                  <span className="tag">UI density</span>
                  <span className="tag">Hide video songs</span>
                  <span className="tag">Hide Shorts</span>
                </div>
              </Reveal>
            </div>
          </div>
        </section>

        {/* ---------- share ---------- */}
        <section className="section section-alt" id="share">
          <div className="container share-grid">
            <Reveal>
              <div className="chapter" style={{ marginBottom: 0 }}>
                <span className="eyebrow">Sharing</span>
                <h2 className="h2">Share anything, anywhere.</h2>
                <p>
                  Every song, playlist, and artist gets a clean{" "}
                  <span style={{ fontFamily: "var(--font-mono)" }}>{SHARE_URL}</span> link.
                  On Android, the link opens straight in the app. Anywhere else, it lands on a
                  beautiful shared page — no registration needed.
                </p>
              </div>
            </Reveal>
            <Reveal delay={120}>
              <div className="share-examples">
                {SHARE_EXAMPLES.map((example, i) => (
                  <ShareUrlRow
                    key={example.label}
                    icon={example.icon}
                    label={example.label}
                    url={example.url}
                    copied={copied === i}
                    onCopy={() => copy(i, example.url)}
                  />
                ))}
                <p className="faint" style={{ fontSize: 13.5, marginTop: 6, paddingInline: 4 }}>
                  Tap a link to copy the pattern. Real links carry the video, list, or channel IDs.
                </p>
              </div>
            </Reveal>
          </div>
        </section>

        {/* ---------- download band ---------- */}
        <section className="section">
          <div className="container">
            <Reveal>
              <div className="download-band">
                <span className="eyebrow" style={{ justifyContent: "center" }}>
                  Download
                </span>
                <h2 className="h2">Pulse Music is free. It always will be.</h2>
                <p className="lead">
                  Grab the latest APK and stay current with updates — no account, no sign-up, no
                  strings.
                </p>
                <div className="band-ctas">
                  <Link className="btn btn-primary btn-lg" to="/download">
                    <Download size={18} /> Get Pulse Music
                  </Link>
                  <a
                    className="btn btn-ghost-light btn-lg"
                    href={LINKS.releases}
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    <Disc3 size={18} /> Latest release
                  </a>
                </div>
                <p className="band-note">
                  Manual updates only — in-app OTA updates have been removed to keep this project
                  free and transparent.
                </p>
              </div>
            </Reveal>
          </div>
        </section>

        {/* ---------- support ---------- */}
        <section className="section section-alt" id="support">
          <div className="container">
            <Reveal>
              <div className="chapter">
                <span className="eyebrow">Support</span>
                <h2 className="h2">Keep the project pulsing.</h2>
                <p>
                  Free to download, free to use, ad-free inside. If Pulse Music makes your day
                  better, a coffee goes a long way.
                </p>
              </div>
            </Reveal>
            <div className="support-grid">
              <Reveal>
                <SupportCard
                  img="/bmac.png"
                  alt="Buy Me a Coffee"
                  title="Buy Me a Coffee"
                  desc="One-time support, zero commitment. The simplest way to say thanks."
                  href={LINKS.buyMeACoffee}
                />
              </Reveal>
              <Reveal delay={80}>
                <SupportCard
                  img="/patreon3.png"
                  alt="Patreon"
                  title="Patreon"
                  desc="Recurring support for the developer behind Pulse Music. Every bit matters."
                  href={LINKS.patreon}
                />
              </Reveal>
              <Reveal delay={160}>
                <SupportCard
                  img="/upi.svg"
                  alt="UPI"
                  title="UPI (India)"
                  desc="Pay directly from any Indian UPI app — instant and no middleman."
                  href={LINKS.upi}
                />
              </Reveal>
            </div>
          </div>
        </section>
      </main>
      <Footer />
    </>
  );
}