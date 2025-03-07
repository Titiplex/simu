<template>
    <div class="map-wrapper">
        <div class="map-container">
            <div v-for="(tile, index) in cityLayer" :key="'city-' + index" class="tile default"
                :style="getIsoPosition(tile.row, tile.col)">
                <button v-on:click="clicked(tile.row, tile.col)" style="background-color: transparent; border: none;">
                    <img :id="'city-' + tile.row + '-' + tile.col" :src="tile.src" alt="City Tile" />
                </button>
            </div>
                <div v-for="(tile, index) in buildingLayer" :key="'building-' + index" class="tile building">
                </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'Map',
    data() {
        return {
            cityLayer: Array.from({ length: 40 * 40 }, (_, i) => {
                const row = Math.floor(i / 40);
                const col = i % 40;
                let fileName;
                const specialPositions = [
                    { row: 23, col: 23 },
                    { row: 17, col: 17 },
                    { row: 32, col: 23 },
                    { row: 26, col: 29 },
                    { row: 26, col: 17 },
                    { row: 20, col: 11 },
                    { row: 26, col: 20 },
                    { row: 29, col: 26 },
                    { row: 17, col: 23 }
                ];
                if (row % 3 === 0 && col % 3 === 0) {
                    fileName = 'landscapeTiles_090.png';
                } else if (row % 3 === 0) {
                    fileName = 'landscapeTiles_074.png';
                } else if (col % 3 === 0) {
                    fileName = 'landscapeTiles_082.png';
                } else {
                    // Vérifier si la position est une position spéciale
                    const isSpecialPosition = specialPositions.some(
                        pos => pos.row === row && pos.col === col
                    );

                    if (isSpecialPosition) {
                        fileName = 'landscapeTiles_067.png'; // Image d'herbe pour les positions spéciales
                    } else {
                        // Pour les autres positions, choisir aléatoirement entre herbe et bâtiments
                        const possibleFiles = [
                            { name: 'landscapeTiles_067.png', weight: 20 },
                            { name: 'buildingTiles_116.png', weight: 25 }, 
                            { name: 'buildingTiles_117.png', weight: 25 }, 
                            { name: 'buildingTiles_125.png', weight: 25 },
                            { name: 'cityTiles_067.png', weight: 5 }
                        ];
                        const totalWeight = possibleFiles.reduce((sum, file) => sum + file.weight, 0);
                        let random = Math.floor(Math.random() * totalWeight);
                        for (const file of possibleFiles) {
                            random -= file.weight;
                            if (random <= 0) {
                                fileName = file.name;
                                break;
                            }
                        }
                    }
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
            const offsetX = 900;
            const offsetY = -900;
            const spacing = 1.3;
            const x = offsetX + (col - row) * (tileWidth / 2) * spacing;
            const y = offsetY + (col + row) * (tileHeight / 2) * spacing;
            return {
                position: 'absolute',
                transform: `translate(${x}px, ${y}px)`
            };
        },
        clicked(row, col) {
            const node = document.getElementById(`city-${row}-${col}`);
            if (!node.classList.contains('hospital')) {
                return;
            }
            // Émettre l'élément pour que MainView puisse récupérer les données du bâtiment
            this.$emit('open', node);
        }
    }
};
</script>

<style scoped>
.map-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    /* Centre verticalement et horizontalement */
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
</style>