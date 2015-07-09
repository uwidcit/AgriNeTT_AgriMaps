from django.contrib.gis.utils import LayerMapping
from models import landParcel

landParcelJsonFile = '/Users/kiran/Downloads/Parcels/parcels.shp'
mapping = {
    'parcelno' : 'PARCELNO',
    'parcelowne' : 'PARCELOWNE',
    'area_acre' : 'AREA_ACRE',
    'area_sf' : 'AREA_SF',
    'area_sm' : 'AREA_SM',
    'area_ha' : 'AREA_HA',
    'landuseno' : 'LANDUSENO',
    'landusetyp' : 'LANDUSETYP',
    'planno' : 'PLANNO',
    'landbookid' : 'LANDBOOKID',
    'errorno' : 'ERRORNO',
    'sheetno' : 'SHEETNO',
    'mapscale' : 'MAPSCALE',
    'geom' : 'MULTIPOLYGON'
}

lm = LayerMapping(landParcel,landParcelJsonFile,mapping)
lm.save(verbose=True)