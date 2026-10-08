import { useEffect, useState } from "react";
import { Check, ChevronDown, Coins, Copy, ExternalLink, ShieldCheck } from "lucide-react";
import { Reveal } from "../components/Reveal";
import { Footer } from "../components/Footer";
import { CRYPTO, LINKS } from "../lib/links";

function CryptoRow({ name, address }: { name: string; address: string }) {
  const [copied, setCopied] = useState(false);

  const copy = () => {
    void navigator.clipboard?.writeText(address);
    setCopied(true);
    window.setTimeout(() => setCopied(false), 1600);
  };

  return (
    <div className="crypto-row">
      <b>{name}</b>
      <span className="crypto-addr">{address}</span>
      <button className="copy-btn" type="button" onClick={copy}>
        {copied ? <Check size={14} /> : <Copy size={14} />}
        {copied ? "Copied" : "Copy address"}
      </button>
    </div>
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
      <span className="btn btn-primary">
        Support <ExternalLink size={16} />
      </span>
    </a>
  );
}

export function Support() {
  const [cryptoOpen, setCryptoOpen] = useState(false);

  useEffect(() => {
    const handler = (e: KeyboardEvent) => {
      if (e.key === "Escape") setCryptoOpen(false);
    };
    window.addEventListener("keydown", handler);
    return () => window.removeEventListener("keydown", handler);
  }, []);

  return (
    <>
      <main>
        <section className="page-hero">
          <div className="container">
            <Reveal>
              <span className="eyebrow">Support</span>
              <h1 className="h1">Free forever. Funded by heart.</h1>
              <p className="lead">
                Pulse Music has no ads, no premium tier, and no subscription — inside the app or on
                this site. Support is purely voluntary and keeps development moving.
              </p>
            </Reveal>
          </div>
        </section>

        <section className="section" style={{ paddingTop: 0 }}>
          <div className="container">
            <Reveal>
              <div className="support-grid">
                <SupportCard
                  img="/bmac.png"
                  alt="Buy Me a Coffee"
                  title="Buy Me a Coffee"
                  desc="A one-time tip. Simple, instant, and every cup genuinely helps."
                  href={LINKS.buyMeACoffee}
                />
                <SupportCard
                  img="/patreon3.png"
                  alt="Patreon"
                  title="Patreon"
                  desc="Recurring support that fuels consistent development and releases."
                  href={LINKS.patreon}
                />
                <SupportCard
                  img="/upi.svg"
                  alt="UPI"
                  title="UPI (India)"
                  desc="Direct payment from any Indian UPI app — no platform cut."
                  href={LINKS.upi}
                />
              </div>
            </Reveal>

            <Reveal delay={80}>
              <div className="crypto" data-open={cryptoOpen}>
                <button
                  className="crypto-summary"
                  type="button"
                  onClick={() => setCryptoOpen((v) => !v)}
                  aria-expanded={cryptoOpen}
                >
                  <span style={{ display: "inline-flex", gap: 12, alignItems: "center" }}>
                    <Coins size={20} style={{ color: "var(--accent)" }} />
                    Cryptocurrency options
                  </span>
                  <ChevronDown className="chev" size={18} />
                </button>
                {cryptoOpen ? (
                  <div className="crypto-body">
                    {CRYPTO.map((c) => (
                      <CryptoRow key={c.name} {...c} />
                    ))}
                  </div>
                ) : null}
              </div>
            </Reveal>

            <Reveal delay={120}>
              <div className="callout" style={{ marginTop: 36, maxWidth: "100%" }}>
                <ShieldCheck size={20} />
                <div>
                  <b>One rule, always.</b> Donations support development — they never unlock features.
                  Every capability in Pulse Music stays free and open source under GPL-3.0, no matter
                  what. Financial contribution is not a purchase of the software; the software is
                  already yours.
                </div>
              </div>
            </Reveal>
          </div>
        </section>
      </main>
      <Footer />
    </>
  );
}