import withExportImages from 'next-export-optimize-images'
import type { NextConfig } from "next";

const nextConfig: NextConfig = {
    output: 'export',
    distDir: "../src/main/resources/static/",
};

export default withExportImages(nextConfig);
