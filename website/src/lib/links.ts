export const WEB_URL = "https://pulsemusic.app";
export const SHARE_URL = "https://share.pulsemusic.app";

export const LINKS = {
  github: "https://github.com/Manish546-gif/PulseMusic",
  releases: "https://github.com/Manish546-gif/PulseMusic/releases/latest",
  discord: "https://discord.gg/Xt5hgsJJuA",
  hello: "mailto:hello@pulsemusic.app",
  security: "mailto:security@pulsemusic.app",
  privacy: "/privacy",
  license: "https://github.com/Manish546-gif/PulseMusic/blob/main/LICENSE",
  buyMeACoffee: "https://buymeacoffee.com/manishmusic",
  patreon: "https://www.patreon.com/cw/manishmusic",
  upi:
    "https://intradeus.github.io/http-protocol-redirector/?r=upi://pay?pa=manishmusic@upi&pn=Manish&am=&tn=Thank%20You",
} as const;

export const CRYPTO = [
  { name: "Bitcoin", address: "bc1qcvyr7eekha8uytmffcvgzf4h7xy7shqzke35fy" },
  { name: "Ethereum", address: "0x51bc91022E2dCef9974D5db2A0e22d57B360e700" },
  { name: "Solana", address: "9wjca3EQnEiqzqgy7N5iqS1JGXJiknMQv6zHgL96t94S" },
];

export function shareSong(id: string): string {
  return `${SHARE_URL}/watch?v=${encodeURIComponent(id)}`;
}

export function sharePlaylist(id: string): string {
  return `${SHARE_URL}/playlist?list=${encodeURIComponent(id)}`;
}

export function shareChannel(id: string): string {
  return `${SHARE_URL}/channel/${encodeURIComponent(id)}`;
}