import { AudioLines, Play, Repeat, Shuffle, SkipBack, SkipForward } from "lucide-react";

export function PhoneMockup() {
  return (
    <div className="hero-media" aria-hidden="true">
      <div className="device">
        <div className="device-screen">
          <div className="device-notch" />
          <div className="np">
            <div className="np-art">
              <span className="np-art-rings" />
              <span className="np-art-rings" />
              <div className="np-eq">
                <span />
                <span />
                <span />
                <span />
              </div>
            </div>
            <div className="np-meta">
              <b>Pulse Music</b>
              <span>Playing — ad-free</span>
            </div>
            <div className="np-progress">
              <div className="np-progress-bar">
                <div className="np-progress-fill" />
              </div>
              <div className="np-times">
                <span>1:24</span>
                <span>3:48</span>
              </div>
            </div>
            <div className="np-controls">
              <Shuffle size={20} strokeWidth={1.8} />
              <SkipBack size={22} strokeWidth={1.8} />
              <span className="np-play">
                <Play size={22} fill="currentColor" />
              </span>
              <SkipForward size={22} strokeWidth={1.8} />
              <Repeat size={20} strokeWidth={1.8} />
            </div>
          </div>
        </div>
      </div>
      <span className="hero-float-chip">
        <AudioLines size={16} />
        Song identified — Pulse Find
      </span>
    </div>
  );
}