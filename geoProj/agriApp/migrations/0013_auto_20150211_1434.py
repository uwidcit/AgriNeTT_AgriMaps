# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('agriApp', '0012_auto_20150211_1421'),
    ]

    operations = [
        migrations.AlterField(
            model_name='soilfeatures',
            name='domsoil',
            field=models.CharField(unique=True, max_length=25),
            preserve_default=True,
        ),
        migrations.AlterField(
            model_name='soils',
            name='domsoil',
            field=models.ForeignKey(to='agriApp.soilFeatures', to_field=b'domsoil', null=True),
            preserve_default=True,
        ),
    ]
