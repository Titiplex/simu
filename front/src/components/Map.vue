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
      cityLayer: Array.from({ length: 10 * 10 }, (_, i) => ({
        row: Math.floor(i / 10),
        col: i % 10,
        src: new URL('@/assets/parts/cityTiles_010.png', import.meta.url).href
      }))
    };
  },
  methods: {
    getIsoPosition(row, col) {
      const tileWidth = 99; 
      const tileHeight = 49.5;
      const offsetX = 300; 
      const offsetY = 100; 
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
  width: 800px;
  height: 600px;
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