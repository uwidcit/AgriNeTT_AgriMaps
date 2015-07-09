# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations
import django.contrib.gis.db.models.fields


class Migration(migrations.Migration):

    dependencies = [
        ('agriApp', '0014_landuse'),
    ]

    operations = [
        migrations.CreateModel(
            name='landParcel',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('parcelno', models.CharField(max_length=20)),
                ('parcelowne', models.CharField(max_length=80)),
                ('area_acre', models.CharField(max_length=25)),
                ('area_sf', models.CharField(max_length=25)),
                ('area_sm', models.CharField(max_length=25)),
                ('area_ha', models.CharField(max_length=25)),
                ('landuseno', models.FloatField()),
                ('landusetyp', models.CharField(max_length=25)),
                ('planno', models.CharField(max_length=25)),
                ('landbookid', models.CharField(max_length=30)),
                ('errorno', models.FloatField()),
                ('sheetno', models.CharField(max_length=25)),
                ('mapscale', models.FloatField()),
                ('geom', django.contrib.gis.db.models.fields.MultiPolygonField(srid=4326)),
            ],
            options={
            },
            bases=(models.Model,),
        ),
    ]
