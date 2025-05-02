import { themes as prismThemes } from "prism-react-renderer";
import type { Config } from "@docusaurus/types";
import type * as Preset from "@docusaurus/preset-classic";

const config: Config = {
  themes: ["@docusaurus/theme-mermaid"],
  markdown: {
    mermaid: true,
  },

  title: "Trading Platform",
  tagline: "Documentation for the Trading Platform",
  favicon: "img/favicon.ico",

  url: "https://trading-platform-rose.vercel.app/",
  baseUrl: "/",

  organizationName: "facebook", // Usually your GitHub org/user name.
  projectName: "TradingPlatform", // Usually your repo name.

  onBrokenLinks: "throw",
  onBrokenMarkdownLinks: "warn",

  i18n: {
    defaultLocale: "en",
    locales: ["en"],
  },

  presets: [
    [
      "classic",
      {
        docs: {
          routeBasePath: "/docs",
          sidebarPath: "./sidebars.ts",
          editUrl:
            "https://github.com/PerkinsDan/trading-platform/tree/main/documentation/docs/",
        },
        theme: {
          customCss: "./src/css/custom.css",
        },
      } satisfies Preset.Options,
    ],
  ],

  themeConfig: {
    // Replace with your project's social card
    image: "img/docusaurus-social-card.jpg",
    navbar: {
      title: "Trading Platform",
      logo: {
        alt: "Trading Platform Logo",
        src: "img/logo.svg",
      },
      items: [
        {
          type: "docSidebar",
          sidebarId: "tutorialSidebar",
          position: "left",
          label: "Docs",
        },
        {
          href: "https://github.com/PerkinsDan/trading-platform",
          label: "GitHub",
          position: "right",
        },
      ],
    },
    footer: {
      style: "dark",
      links: [
        {
          title: "Docs",
          items: [
            {
              label: "Intro",
              to: "/docs/intro",
            },
            {
              label: "Apps (Market Data)",
              to: "/docs/Apps/MarketData/MarketDataServiceMain",
            },
            {
              label: "Apps (Order Processor)",
              to: "docs/Apps/OrderProcessor",
            },
            {
              label: "Architecture",
              to: "docs/Diagrams/architecture",
            },
          ],
        },
      ],
    },
    prism: {
      theme: prismThemes.github,
      darkTheme: prismThemes.dracula,
    },
  } satisfies Preset.ThemeConfig,
};

export default config;
