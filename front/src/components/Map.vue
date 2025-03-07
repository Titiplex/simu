<template>
  <div class="map-wrapper">
    <div class="map-container">
      <div 
        v-for="(tile, index) in cityLayer" 
        :key="'city-'+index" 
        class="tile" 
        :style="getIsoPosition(tile.row, tile.col)"
      >
        <img :src="tile.src" alt="City Tile" />
      </div>
      <div 
        v-for="(tile, index) in buildingLayer" 
        :key="'building-'+index" 
        class="tile building" 
        :style="getIsoPosition(tile.row, tile.col)"
      >
        <img :src="tile.src" alt="Building Tile" />
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Map',
  data() {
    return {
      cityLayer: Array.from({ length: 30 * 30 }, (_, i) => {
        const row = Math.floor(i / 30);
        const col = i % 30;
        let fileName;

        if (row % 3 === 0 && col % 3 === 0) {
          fileName = 'landscapeTiles_090.png';
        } else if (row % 3 === 0) {
          fileName = 'landscapeTiles_074.png';
        } else if (col % 3 === 0) {
          fileName = 'landscapeTiles_082.png';
        } else {
          fileName = 'landscapeTiles_067.png';
        }

        const src = `/src/assets/parts/${fileName}`;
        return {
          row,
          col,
          src
        };
      }),
      buildingLayer: []
    };
  },
  methods: {
    getIsoPosition(row, col) {
      const tileWidth = 99;
      const tileHeight = 49.5;
      const offsetX = 750;
      const offsetY = -500;
      const spacing = 1.3;
      const x = offsetX + (col - row) * (tileWidth / 2) * spacing;
      const y = offsetY + (col + row) * (tileHeight / 2) * spacing;
      return {
        position: 'absolute',
        transform: `translate(${x}px, ${y}px)`
      };
    }
  }
};
</script>

<style scoped>
.map-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh; /* Centre verticalement et horizontalement */
}

.map-container {
  position: relative;
  width: 100%;
  height: 100%;
  background: #ccc;
  overflow: hidden;
}

.tile {
  width: 32px;
  height: 32px;
}

.building {
  z-index: 2;
}
</style>