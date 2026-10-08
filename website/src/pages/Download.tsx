import { Link } from "react-router-dom";
import { Check, Download as DownloadIcon, ExternalLink, ShieldCheck, Smartphone, Wrench } from "lucide-react";
import { Reveal } from "../components/Reveal";
import { Footer } from "../components/Footer";
import { LINKS } from "../lib/links";

function VariantCard({
  icon: Icon,
  title,
  desc,
  features,
  badge,
}: {
  icon: typeof Smartphone;
  title: string;
  desc: string;
  features: string[];
  badge?: string;
}) {
  return (
    <div className="variant-card">
      <span className="fc-icon">
        <Icon size={22} />
      </span>
      <h3 className="h3">
        {title} {badge ? <em style={{ color: "var(--accent)", fontStyle: "normal" }}>{badge}</em> : null}
      </h3>
      <p className="desc">{desc}</p>
      <ul className="variant-features">
        {features.map((f) => (
          <li key={f}>
            <Check size={16} />
            {f}
          </li>
        ))}
      </ul>
      <a className="btn btn-ghost" href={LINKS.releases} target="_blank" rel="noopener noreferrer">
        <DownloadIcon size={16} /> Get the APK <ExternalLink size={14} />
      </a>
    </div>
  );
}

export function Download() {
  return (
    <>
      <main>
        <section className="page-hero">
          <div className="container">
            <Reveal>
              <span className="eyebrow">Download</span>
              <h1 className="h1">Free to get. Free to keep.</h1>
              <p className="lead">
                Pulse Music ships manually — no store tiers, no paid versions. Get the latest APK
                from the official release page. Updates arrive there too.
              </p>
            </Reveal>
          </div>
        </section>

        <section className="section" style={{ paddingTop: 0 }}>
          <div className="container">
            <Reveal>
              <div className="variant-grid">
                <VariantCard
                  icon={ShieldCheck}
                  title="FOSS build"
                  desc="The fully open-source build with no Google Play Services. Maximum privacy, minimum footprint."
                  features={[
                    "No Google services bundled",
                    "Universal APK for all devices",
                    "Everything: lyrics, offline, Pulse Find",
                  ]}
                  badge="Free & open source"
                />
                <VariantCard
                  icon={Smartphone}
                  title="GMS build"
                  desc="Includes Google Play Services for Cast support — send playback to Cast-enabled speakers and TVs."
                  features={[
                    "Google Cast support built in",
                    "Same features as FOSS",
                    "Choose what fits your setup",
                  ]}
                />
              </div>
            </Reveal>

            <Reveal delay={80}>
              <h2 className="h2" style={{ marginTop: 80, marginBottom: 28 }}>
                Install in three steps
              </h2>
              <ol className="steps">
                <li>
                  <div>
                    <b>Download the APK</b> from the official releases page above. Only grab builds
                    from the repositories we link — don't trust random APK mirrors.
                  </div>
                </li>
                <li>
                  <div>
                    <b>Allow unknown sources</b> when Android asks. Tap the downloaded file, follow the
                    prompt, and the app installs like any other.
                  </div>
                </li>
                <li>
                  <div>
                    <b>Check for updates here.</b> In-app OTA updates were removed, so this website is
                    the single source for new versions.
                  </div>
                </li>
              </ol>
            </Reveal>

            <Reveal delay={120}>
              <div className="callout" style={{ marginTop: 36 }}>
                <Wrench size={20} />
                <div>
                  <b>Building from source?</b> The repo includes two build variants and every module
                  you need. Follow{" "}
                  <Link to={LINKS.github} target="_blank" rel="noopener noreferrer" style={{ textDecoration: "underline" }}>
                    SETUP.md
                  </Link>{" "}
                  for SDK configuration, signing, and optimized ARM64 builds.
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