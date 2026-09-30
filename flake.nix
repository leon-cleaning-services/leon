{
  description = "Development shell for leon";

  # Stable release. Bump to the next one (nixos-YY.MM) every six months.
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-26.05";

  outputs =
    { nixpkgs, ... }:
    let
      systems = [
        "aarch64-darwin"
        "x86_64-darwin"
        "aarch64-linux"
        "x86_64-linux"
      ];
      forAllSystems = f: nixpkgs.lib.genAttrs systems (system: f nixpkgs.legacyPackages.${system});
    in
    {
      # Ruby for fastlane (Gemfile). The JDK and the Android SDK come from the machine, and
      # Gradle downloads the toolchains it needs. Loaded by direnv (.envrc) or `nix develop`.
      devShells = forAllSystems (pkgs: {
        default = pkgs.mkShell {
          packages = [ pkgs.ruby ];
          # The Nix store is read-only, so gems (and the Bundler version from Gemfile.lock) go
          # to the home directory
          shellHook = ''
            export GEM_HOME="$HOME/.local/share/gem/ruby/${pkgs.ruby.version.libDir}"
            export PATH="$GEM_HOME/bin:$PATH"
          '';
        };
      });
    };
}
